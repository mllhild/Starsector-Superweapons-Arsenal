package data.scripts.weapons;

import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import java.awt.Color;

public class FlashEveryFrameEffect implements EveryFrameWeaponEffectPlugin {
	
    private static final Color MUZZLE_FLASH_COLOR = new Color(130, 160, 255, 100);
	float MAX_DAMAGE_MULT = 1.3f;

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
		weapon.getSpec().setUnaffectedByProjectileSpeedBonuses(true);
		
        if (engine.isPaused())
            return;
		
		for (DamagingProjectileAPI p : engine.getProjectiles()){
			
			if(p.getWeapon() == weapon){
				float Damage = p.getProjectileSpec().getDamage().getBaseDamage();
				
				if (p.getElapsed() < 0.4f)
					p.setDamageAmount(Damage + (Damage * p.getElapsed()/0.8f));
				else
					p.setDamageAmount(Damage * MAX_DAMAGE_MULT);
			}
		}
		
		if (Global.getSettings().getCurrentState() == GameState.COMBAT){
			float chargeLevel = weapon.getChargeLevel();
			float shipFacing = weapon.getCurrAngle();
			Vector2f weaponLocation = weapon.getLocation();
			Vector2f shipVelocity = weapon.getShip().getVelocity();
			Vector2f muzzleLocation = MathUtils.getPointOnCircumference(weaponLocation, 45f, shipFacing);
				
			if (chargeLevel == 1f){
					
				RippleDistortion ripple = new RippleDistortion(muzzleLocation, shipVelocity);
				ripple.setSize(400f);
				ripple.setIntensity(40f);
				ripple.setFrameRate(30f);
				ripple.fadeInSize(0.5f);
				ripple.fadeOutIntensity(0.5f);
				DistortionShader.addDistortion(ripple);
					
				engine.spawnExplosion(muzzleLocation, shipVelocity, MUZZLE_FLASH_COLOR, 250f, 0.4f);
				Global.getSoundPlayer().playUISound("Flash_Fire", 1f, 0.5f);
			}
		}
	}
}
