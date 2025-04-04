package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import org.lazywizard.lazylib.MathUtils;
import com.fs.starfarer.api.GameState;
import org.lwjgl.util.vector.Vector2f;
import java.awt.Color;

public class ReflectiveShieldEveryFrame implements EveryFrameWeaponEffectPlugin {

    private static final Color COLOR = new Color(80, 80, 250, 225);
	float counter = 0f;
	
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (engine.isPaused()) 
            return;
		
		ShipAPI ship = weapon.getShip();
		
		if (!weapon.getSlot().isHardpoint() && ship.getShield() != null)
			ship.getVariant().addMod("sw_reflective_shield");
		
		if (ship.getShield() == null)
			ship.getVariant().removeMod("sw_reflective_shield");
		
		
		if (ship.getVariant().hasHullMod("sw_reflective_shield")){
			String innerSprite;
			String outerSprite;
		
			float Radius = ship.getShield().getRadius();
		
			if (Radius >= 256f) {
				innerSprite = "graphics/shield/sw_shields256.png";
				outerSprite = "graphics/shield/sw_shields256ring.png";
			} else if (Radius >= 128f) {
				innerSprite = "graphics/shield/sw_shields128.png";
				outerSprite = "graphics/shield/sw_shields128ring.png";
			} else {
				innerSprite = "graphics/shield/sw_shields64.png";
				outerSprite = "graphics/shield/sw_shields64ring.png";
			}
			
			ship.getShield().setRadius(Radius, innerSprite, outerSprite);
			ship.getShield().setInnerRotationRate(2f);
		}
		
		if (Global.getSettings().getCurrentState() == GameState.COMBAT){
			counter += amount;
			
			if (ship.getShield() != null)
				if (ship.getShield().isOn() && !weapon.getSlot().isHardpoint()){
					Vector2f center = weapon.getLocation();
				
					if (counter > 0.3f){
						counter = 0f;
						engine.spawnEmpArc(ship, MathUtils.getRandomPointInCircle(center , 30f), null, new SimpleEntity(MathUtils.getRandomPointInCircle(center , 30f)),
							DamageType.ENERGY,
							0f,
							0f,
							100000f,
							null,
							7f,
							COLOR,
							COLOR.brighter());
				}
			}
		}
	}
}


