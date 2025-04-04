package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.DamageType;
import org.lwjgl.util.vector.Vector2f;

import java.awt.Color;

public class DevastatorBeamEffect implements BeamEffectPlugin {

    private static final Color COLOR = new Color(80, 80, 250, 225);
	private static final Color COLOR2 = new Color(125, 125, 250, 225);
    private boolean runOnce = true;
    private float Damage = 0f;
	private float Width = 0f;
	private float counter = 0f;
	private float counter2 = 0f;
	private float counter3 = 0f;

    @Override
    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
		counter2 += amount;
		counter3 += amount;

        if (runOnce) {
            Damage = beam.getWeapon().getDamage().getDamage() * 1.3f;
			Width = beam.getWidth();
			runOnce = false;
        }

        if (beam.getWeapon().getChargeLevel() < 1f && counter < 1.5f) {
            beam.setWidth(20f);
			beam.getDamage().setDamage(Damage * 0.1f);
			counter += amount;
        }
		else {
            beam.setWidth(Width);
			beam.getDamage().setDamage(Damage);
        }
		
		if (beam.getWeapon().getChargeLevel() == 0){
			counter = 0;
		}
		
		
		if (beam.getWeapon().isFiring() && beam.getWeapon().getChargeLevel() == 1){
			if (beam.getDamageTarget() != null && beam.getDamageTarget() instanceof ShipAPI){
				if (beam.getDamageTarget().getShield() != null && beam.getDamageTarget().getShield().isWithinArc(beam.getTo())){
					if (counter2 > 0.1f){
						counter2 = 0f;
						
						engine.spawnEmpArcPierceShields(beam.getSource(), beam.getTo(), beam.getDamageTarget(), beam.getDamageTarget(),
							DamageType.ENERGY,
							Damage * 0.04f,
							Damage * 0.05f, // emp 
							100000f, // max range 
							null,
							45f, // thickness
							COLOR,
							COLOR2);
							
							engine.spawnEmpArc(beam.getSource(), beam.getTo(), beam.getDamageTarget(), beam.getDamageTarget(),
							DamageType.ENERGY,
							Damage * 0.025f,
							0f, // emp 
							100000f, // max range 
							null,
							50f, // thickness
							COLOR,
							COLOR2);
					}
				}
				else{
					if (counter2 > 0.1f){
						counter2 = 0f;
						
						engine.spawnEmpArc(beam.getSource(), beam.getTo(), beam.getDamageTarget(), beam.getDamageTarget(),
							DamageType.ENERGY,
							Damage * 0.004f,
							Damage * 0.1f, // emp 
							100000f, // max range 
							null,
							45f, // thickness
							COLOR,
							COLOR2);
					}
				}
			}
			if (counter3 > 0.1f){
				counter3 = 0f;
				engine.spawnEmpArcPierceShields(beam.getSource(), beam.getFrom(), beam.getSource(), beam.getSource(),
							DamageType.ENERGY,
							Damage * 0.015f,
							Damage * 0.015f, // emp 
							100000f, // max range 
							null,
							45f, // thickness
							COLOR,
							COLOR2);
			}
		}
	}
}

