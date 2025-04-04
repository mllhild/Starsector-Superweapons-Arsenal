package data.scripts.weapons;

import org.lwjgl.util.vector.Vector2f;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.Global;
import java.awt.Color;
import data.scripts.util.MagicFakeBeam;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import com.fs.starfarer.api.GameState;

public class LightningStormEveryFrame implements EveryFrameWeaponEffectPlugin {
	
    private static final Color GLOW_COLOR = new Color(170, 180, 255, 110);
    private static final Color BEAM_COLOR = new Color(170, 180, 255, 80);
    private static final int PARTICLE_COUNT = 6;
    private static final float PARTICLE_SIZE = 6f;
    private static final float PARTICLE_DURATION = 1f;
	
    private static final float MUZZLE_OFFSET_HARDPOINT = 60f;
    private static final float MUZZLE_OFFSET_TURRET = 55f;
	
	private float counter = 0f;
	private float counter2 = 0f;
	private boolean shot = false;
	private boolean charging = false;
	private int TeslaCoils;

	//Everything in this script is pure visuals, for Lightning Storm damage check the java file in the plugins folder.

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
		weapon.getSpec().setUnaffectedByProjectileSpeedBonuses(true);
        if (engine.isPaused())
            return;
		
		TeslaCoils = 0;
		
		if (Global.getSettings().getCurrentState() == GameState.COMBAT){
			float chargeLevel = weapon.getChargeLevel();
			
			if (weapon.isFiring() && !shot){
				counter += amount;
				counter2 += amount;
				
				Vector2f weaponLocation = weapon.getLocation();
				ShipAPI ship = weapon.getShip();
				float shipFacing = weapon.getCurrAngle();
				Vector2f shipVelocity = ship.getVelocity();
				
				Vector2f muzzleLocation = MathUtils.getPointOnCircumference(weaponLocation,
                weapon.getSlot().isHardpoint() ? MUZZLE_OFFSET_HARDPOINT : MUZZLE_OFFSET_TURRET, shipFacing);
			
				if(counter > 0.1f){
					counter = 0f;
					Vector2f targetOffset = MathUtils.getRandomPointOnCircumference(muzzleLocation, 1f);
					
					engine.spawnEmpArc(weapon.getShip(), muzzleLocation , null, new SimpleEntity(targetOffset), 
						DamageType.ENERGY, 
						0f,
						0f, 
						100000f,
						null,
						weapon.getChargeLevel() * 10f,
						GLOW_COLOR,
						GLOW_COLOR);
			
					float distance, angle, speed;
					Vector2f particleVelocity;
					
					for (int i = 0; i < PARTICLE_COUNT; ++i) {
						distance = MathUtils.getRandomNumberInRange(20f, 100f);
						angle = MathUtils.getRandomNumberInRange(-0.5f * 360f, 0.5f * 360f);
						speed = distance / PARTICLE_DURATION;
						particleVelocity = MathUtils.getPointOnCircumference(shipVelocity, speed, 180.0f + angle + shipFacing);
						Vector2f spawnLocation = MathUtils.getPointOnCircumference(muzzleLocation, distance, (angle + shipFacing));
						engine.addHitParticle(spawnLocation, particleVelocity, PARTICLE_SIZE, 1f, PARTICLE_DURATION, GLOW_COLOR);
					}
					
					//Additional effects when Tesla Coils are mounted
					for (WeaponAPI weapons : ship.getAllWeapons()){
						if (weapons.getSpec().hasTag("sw_tesla_coil")){
							TeslaCoils++;
							MagicFakeBeam.spawnFakeBeam(
								engine, 														//CombatEngineAPI
								weapons.getLocation(), 											//from
								MathUtils.getDistance(weapons.getLocation(),muzzleLocation), 	//range
								VectorUtils.getAngle(weapons.getLocation(),muzzleLocation),		//Angle
								40f, 															//Width
								0.1f, 															//duration
								0.5f, 															//fade-out duration
								25f, 															//Impactsize
								BEAM_COLOR.brighter(), 											//Core color
								BEAM_COLOR, 													//Fringe color
								0f, 															//Damage
								DamageType.ENERGY, 												//Damage Type
								0f, 															//EMP
								ship															//Ship Source
							);	
						}//Unironically this wouldn't be possible without both MagicLib and Lazylib.
					}
				}
				
				
				if (TeslaCoils > 0 && !charging){
					charging = true;
					Global.getSoundPlayer().playSound("Lightning_Storm_Charge2", 1f, 1f, weapon.getLocation(), new Vector2f());
				}
				
				if (chargeLevel == 1f && !shot){
					shot = true;
					
					RippleDistortion ripple = new RippleDistortion(muzzleLocation, ship.getVelocity());
					ripple.setSize(300f);
					ripple.setIntensity(30f);
					ripple.setFrameRate(30f);
					ripple.fadeInSize(0.5f);
					ripple.fadeOutIntensity(0.5f);
					DistortionShader.addDistortion(ripple);
					
					engine.spawnExplosion(muzzleLocation, shipVelocity, GLOW_COLOR, 200f, 0.3f);
					engine.addSmoothParticle(muzzleLocation, shipVelocity, 300f * 3f, 1f, 0.5f, GLOW_COLOR);
					
					for (int i = 0; i < 3; ++i) {
						Vector2f Loc = MathUtils.getRandomPointInCircle(muzzleLocation, 200f + (float) Math.random() * 100f);
						engine.spawnEmpArc(ship, muzzleLocation, new SimpleEntity(muzzleLocation), new SimpleEntity(Loc),
                            DamageType.ENERGY, 0f, 0f, 1000f, null, chargeLevel * 15f + 15f, GLOW_COLOR, GLOW_COLOR);
					}
				}
			}
			if (chargeLevel == 0f){
				charging = false;
				shot = false;
			}
		}
	}
}
