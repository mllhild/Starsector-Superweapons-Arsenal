package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.DamageType;
import org.lwjgl.util.vector.Vector2f;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;

public class PrismBeamEffect implements BeamEffectPlugin {

    private static final Color COLOR = new Color(130, 130, 220, 225);
	private static final Color COLOR2 = new Color(145, 145, 250, 225);
    private static final Vector2f ZERO = new Vector2f();
	private static final float SEARCH_RANGE = 750f;
	List<ShipAPI> TARGETS = new ArrayList();
	List<ShipAPI> FIGHTERS = new ArrayList();
	private boolean triggered = false;
	Vector2f point = new Vector2f();
	float ARC_Damage = 0f;
	
    @Override
    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
		
		ShipAPI Ship = beam.getSource();
		ARC_Damage = beam.getWeapon().getDamage().getDamage() * 0.08f;
		beam.getDamage().setForceHardFlux(true);
		
		if (beam.getWeapon().getChargeLevel() == 1 && beam.getDamageTarget() != null && !triggered){
			triggered = true;
			
				point.setX(beam.getTo().getX());
				point.setY(beam.getTo().getY());
				
			for (ShipAPI Target : CombatUtils.getShipsWithinRange(point, SEARCH_RANGE))
				if (Target != Ship &&
					Target.isAlive() &&
					Target != beam.getDamageTarget() &&
					!Target.isAlly() &&
					!Target.isFighter() &&
					Target.getOriginalOwner() != Ship.getOriginalOwner())
					TARGETS.add(Target);
					
			for (ShipAPI Fighter : CombatUtils.getShipsWithinRange(point, SEARCH_RANGE))
				if (Fighter.isFighter() &&
					Fighter.isAlive() &&
					!Fighter.isAlly() &&
					Fighter.getOriginalOwner() != Ship.getOriginalOwner())
					FIGHTERS.add(Fighter);
				
			Collections.shuffle(TARGETS);
			Collections.shuffle(FIGHTERS);
			
			for (int i = 0; i < 5; i++){
				
				if (i < TARGETS.size()){
					for (int s = 0; s < 3; s++)
						engine.spawnEmpArc(Ship, point, (ShipAPI) TARGETS.get(i), (ShipAPI) TARGETS.get(i),
						DamageType.ENERGY,
						ARC_Damage,
						0f,
						100000f,
						null,
						45f,
						COLOR,
						COLOR2.brighter());
				}
				
				else if (i < FIGHTERS.size()){
					for (int s = 0; s < 3; s++)
						engine.spawnEmpArc(Ship, point, (ShipAPI) FIGHTERS.get(i), (ShipAPI) FIGHTERS.get(i),
						DamageType.ENERGY,
						ARC_Damage,
						0f,
						100000f,
						null,
						45f,
						COLOR,
						COLOR2.brighter());
				}
				
				else{
					float Offset = 400f + (float) Math.random() * 150f;
					
					Vector2f Loc = new Vector2f(point.x + Offset, point.y);
					Loc = VectorUtils.rotateAroundPivot(Loc, point, (float) Math.random() * 360f, Loc);
					
					for (int s = 0; s < 2; s++)
						engine.spawnEmpArc(Ship, point, null, new SimpleEntity(Loc),
						DamageType.ENERGY,
						ARC_Damage,
						0f,
						100000f,
						null,
						30f,
						COLOR,
						COLOR2.brighter());
				}
			}
			engine.spawnExplosion(point, ZERO, COLOR, 400f, 1.1f);
			engine.spawnExplosion(point, ZERO, COLOR2, 220f, 0.85f);
			Global.getSoundPlayer().playSound("Gauss_Explode", 1f, 1f, point, ZERO);
		}
	}
}

