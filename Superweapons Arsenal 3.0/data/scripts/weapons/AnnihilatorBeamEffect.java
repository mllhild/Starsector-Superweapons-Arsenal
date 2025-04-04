package data.scripts.weapons;

import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import org.dark.shaders.distortion.RippleDistortion;
import org.dark.shaders.distortion.DistortionShader;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import org.lazywizard.lazylib.combat.CombatUtils;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.Global;

import java.util.ArrayList;
import java.awt.Color;
import java.util.List;
import java.util.*;

public class AnnihilatorBeamEffect implements BeamEffectPlugin {
	
	private static final Color EXPLOSION_COLOR = new Color(220, 100, 100 , 255);
    private static final Color COLOR = new Color(255, 120, 120, 225);
    private static final Vector2f ZERO = new Vector2f();
	List<ShipAPI> TARGETS = new ArrayList();
	Vector2f point = new Vector2f();
	private final float RADIUS = 1250f;
	private boolean fullCharge = false;
    private boolean runOnce = false;
	private boolean blast = false;
	private boolean flag = true;
	private float counter = 0f;
	private float Damage = 0f;
	private float Width2 = 0f;
	private float Width = 0f;

    @Override
    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
		
		beam.getDamage().setForceHardFlux(true);
		
        if (!runOnce) {
			Damage = beam.getWeapon().getDamage().getDamage() * 1f;
			Width2 = beam.getWidth();
			Width = beam.getWidth();
			runOnce = true;
        }
		
		WeaponAPI Weapon = beam.getWeapon();
		ShipAPI Ship = Weapon.getShip();
		
		if (beam.getWeapon().getChargeLevel() == 1f){
			
			if (flag){
				Global.getSoundPlayer().playUISound("Annihilator_Fire", 1f, 1f);
				flag = false;
			}
			
			beam.getDamage().setDamage(Damage * 1.2f);
            beam.setWidth(Width);
			fullCharge = true;
			counter += amount;
			
			point.setX(beam.getTo().getX());
			point.setY(beam.getTo().getY());
			
			if (counter > 0.05f && beam.getDamageTarget() != null){
				counter = 0f;
				
				TARGETS.clear();
				
				for (ShipAPI Target : CombatUtils.getShipsWithinRange(point, RADIUS))
					if (Target != Ship && Target.isAlive() && Target != beam.getDamageTarget())
						TARGETS.add(Target);
				
				if (TARGETS.size() > 0){
					Collections.shuffle(TARGETS);
					
					engine.spawnEmpArc(beam.getSource(), point, (ShipAPI)TARGETS.get(0), (ShipAPI)TARGETS.get(0),
							DamageType.ENERGY,
							375f, //damage
							10f,  // emp 
							100000f, // max range 
							null,
							75f, // thickness
							COLOR,
							COLOR.brighter());
					
				}
				else{
					float Offset = 300f + (float) Math.random() * 700f;
					
					Vector2f Loc = new Vector2f(point.x + Offset, point.y);
					Loc = VectorUtils.rotateAroundPivot(Loc, point, (float) Math.random() * 360f, Loc);
					
					engine.spawnEmpArc(Ship, point, null, new SimpleEntity(Loc),
						DamageType.ENERGY,
						0f,
						0f,
						100000f,
						null,
						75f, // thickness
						COLOR,
						COLOR.brighter());
				}
			}
		}
		else if (beam.getWeapon().getChargeLevel() < 1f && !fullCharge){
			beam.getDamage().setDamage(Damage / 2f);
			beam.setWidth(Width / 2.5f);
		}
		else {
			Width2 = Width2 - 1f;
			beam.getDamage().setDamage(Damage / 2f);
			beam.setWidth(Math.max(Width2,Width/2.5f));
		}
		
		
		//Explosion
		if (fullCharge && beam.getWeapon().getChargeLevel() < 1f && !blast && beam.getTo() != null){
			blast = true;
			
			for (ShipAPI target : CombatUtils.getShipsWithinRange(point, RADIUS - 250f)){
				
				for (int i = 0; i < 10; i++)
					engine.spawnEmpArcPierceShields(beam.getSource(), point, target, target,
							DamageType.ENERGY,
							30f,
							1000f, // emp 
							100000f, // max range 
							null,
							0f, // thickness
							COLOR,
							COLOR.brighter());
			}
			
			RippleDistortion ripple = new RippleDistortion(point, ZERO);
				ripple.setSize(1500f);
				ripple.setIntensity(400f);
				ripple.setFrameRate(30f);
				ripple.fadeInSize(0.8f);
				ripple.fadeOutIntensity(0.5f);
			
			engine.spawnExplosion(point, ZERO, EXPLOSION_COLOR, 		   3500f, 2.2f);
			engine.spawnExplosion(point, ZERO, EXPLOSION_COLOR.brighter(), 2000f, 1.3f);
			Global.getSoundPlayer().playUISound("Annihilator_Explode", 1f, 1f);
			DistortionShader.addDistortion(ripple);
		}
	}
}

