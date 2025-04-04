package data.scripts.weapons;

import org.lwjgl.util.vector.Vector2f;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.Global;
import java.awt.Color;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import com.fs.starfarer.api.GameState;

public class LightSpearEveryFrame implements EveryFrameWeaponEffectPlugin {
	
    private static final Color PARTICLE_COLOR = new Color(100, 140, 255, 100);
    private static final int PARTICLE_COUNT = 5;
    private static final float PARTICLE_SIZE = 7.5f;
    private static final float PARTICLE_DURATION = 1f;
	
    private static final Color MUZZLE_FLASH_COLOR = new Color(100, 140, 255, 100);
    private static final float MUZZLE_OFFSET_HARDPOINT = 33f;
    private static final float MUZZLE_OFFSET_TURRET = 31f;
	
	private float counter = 0f;
	private boolean shot = false;

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (engine.isPaused()) {
            return;
		}
		
		if (Global.getSettings().getCurrentState() == GameState.COMBAT){
			float chargeLevel = weapon.getChargeLevel();
			
			if (weapon.isFiring() && !shot){
				counter += amount;
				
				Vector2f weaponLocation = weapon.getLocation();
				ShipAPI ship = weapon.getShip();
				float shipFacing = weapon.getCurrAngle();
				Vector2f shipVelocity = ship.getVelocity();
				
				Vector2f muzzleLocation = MathUtils.getPointOnCircumference(weaponLocation,
                    weapon.getSlot().isHardpoint() ? MUZZLE_OFFSET_HARDPOINT : MUZZLE_OFFSET_TURRET, shipFacing);
			
				if (counter > 0.1f){
					counter = 0f;
					float distance, angle, speed;
					Vector2f particleVelocity;
					
					for (int i = 0; i < PARTICLE_COUNT; ++i) {
						distance = MathUtils.getRandomNumberInRange(20f, 100f);
						angle = MathUtils.getRandomNumberInRange(-0.5f * 360f, 0.5f * 360f);
						speed = distance / PARTICLE_DURATION;
						particleVelocity = MathUtils.getPointOnCircumference(shipVelocity, speed, 180.0f + angle + shipFacing);
						Vector2f spawnLocation = MathUtils.getPointOnCircumference(muzzleLocation, distance, (angle + shipFacing));
						engine.addHitParticle(spawnLocation, particleVelocity, PARTICLE_SIZE, 1f, PARTICLE_DURATION, PARTICLE_COLOR);
					}
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
					
					engine.spawnExplosion(muzzleLocation, shipVelocity, MUZZLE_FLASH_COLOR, 200f, 0.3f);
					engine.addSmoothParticle(muzzleLocation, shipVelocity, 300f * 3f, 1f, 0.5f, MUZZLE_FLASH_COLOR);
					
					for (int i = 0; i < 3; ++i) {
						Vector2f Loc = MathUtils.getRandomPointInCircle(muzzleLocation, 200f + (float) Math.random() * 100f);
						engine.spawnEmpArc(ship, muzzleLocation, new SimpleEntity(muzzleLocation), new SimpleEntity(Loc),
                            DamageType.ENERGY, 0f, 0f, 1000f, null, chargeLevel * 15f + 15f, PARTICLE_COLOR, MUZZLE_FLASH_COLOR);
					}
				}
			}
			if (chargeLevel == 0f)
				shot = false;
		}
	}
}
