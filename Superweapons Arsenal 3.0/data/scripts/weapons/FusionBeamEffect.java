package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.DamageType;
import org.lwjgl.util.vector.Vector2f;
import java.awt.Color;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;

public class FusionBeamEffect implements EveryFrameWeaponEffectPlugin {

	private static final Color COLOR = new Color(175, 175, 235, 235);
	Vector2f point = new Vector2f();
	float counter = 0f;
	
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
		if (engine.isPaused()) {return;}
		
		if (weapon.getChargeLevel() == 1){
			
			BeamAPI beam = (BeamAPI) weapon.getBeams().get(0);
			ShipAPI Ship = weapon.getShip();
		
			beam.getDamage().setForceHardFlux(true);
		
			counter += amount;
			point = beam.getTo();
				
			if (counter > 0.2f){
				counter = 0f;
				engine.spawnEmpArc(Ship, beam.getFrom(), null, new SimpleEntity(point),
						DamageType.ENERGY,
						0f,
						0f,
						100000f,
						null,
						65f,
						COLOR,
						COLOR.brighter());
				}
		}
	}
}

