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

public class SupernovaEveryFrame implements EveryFrameWeaponEffectPlugin {
	
    private static final Color MUZZLE_FLASH_COLOR = new Color(220, 150, 50, 120);
    private static final float MUZZLE_OFFSET_HARDPOINT = 28f;
    private static final float MUZZLE_OFFSET_TURRET = 20f;
	float counter = 0f;

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (engine.isPaused()) {
            return;
		}
		
		counter += amount;
		
		if (Global.getSettings().getCurrentState() == GameState.COMBAT){
			
			float chargeLevel = weapon.getChargeLevel();
			Vector2f weaponLocation = weapon.getLocation();
			ShipAPI ship = weapon.getShip();
			float shipFacing = weapon.getCurrAngle();
			Vector2f shipVelocity = ship.getVelocity();
				
			Vector2f muzzleLocation = MathUtils.getPointOnCircumference(weaponLocation,
            weapon.getSlot().isHardpoint() ? MUZZLE_OFFSET_HARDPOINT : MUZZLE_OFFSET_TURRET, shipFacing);
			
			
			if (weapon.isFiring() && chargeLevel == 1f){
				
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
                    DamageType.ENERGY, 0f, 0f, 1000f, null, chargeLevel * 15f + 15f, MUZZLE_FLASH_COLOR, MUZZLE_FLASH_COLOR);
				}
			}
			else if(!weapon.isFiring() && counter > 0.1f && weapon.getShip().isAlive() && weapon.getAmmo() > 0){
				
				counter = 0f;
				Vector2f targetOffset = MathUtils.getRandomPointOnCircumference(muzzleLocation, 1f);
					
				engine.spawnEmpArc(weapon.getShip(), muzzleLocation , null, new SimpleEntity(targetOffset), 
					DamageType.ENERGY, 
					0f,
					0f, 
					100000f,
					null,
					20f,
					MUZZLE_FLASH_COLOR,
					MUZZLE_FLASH_COLOR);
			}
		}
	}
}
