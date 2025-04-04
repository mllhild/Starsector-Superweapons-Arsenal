package data.scripts.weapons;

import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CollisionClass;
import org.lazywizard.lazylib.combat.CombatUtils;
import data.scripts.plugins.SW_BlackHolePlugin;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import org.lazywizard.lazylib.MathUtils;
import data.scripts.util.MagicRender;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.Global;

import org.lwjgl.util.vector.Vector2f;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Color;
import java.util.List;
import java.util.Map;

public class BlackHoleBeamEffect implements BeamEffectPlugin {
	
	private static final Color EXPLOSION_COLOR = new Color(90, 60, 255 , 255);
	private static final Color EMPTY = new Color(0, 0, 0 , 0);
	private static final Vector2f SIZE = new Vector2f(2048,2048);
	private float BLACKHOLE_RADIUS = 2000f;
	private float IMPLODE_DAMAGE = 8000f;
	Vector2f point = new Vector2f();
	private boolean Implode = false;
    private boolean runOnce = false;
	private boolean fired = false;
	private float Damage = 0f;
	private float Width2 = 0f;
	private float Width = 0f;

    @Override
    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
		float power = BLACKHOLE_RADIUS / 80f;
		
        if (!runOnce) {
			Damage = beam.getWeapon().getDamage().getDamage() * 1f;
			Width = beam.getWidth();
			Width2 = beam.getWidth();
			runOnce = true;
        }
		
		WeaponAPI Weapon = beam.getWeapon();
		ShipAPI Ship = Weapon.getShip();
		
		//Lock movement
		if(Weapon.isFiring() && !fired){
			Ship.getMutableStats().getMaxTurnRate().modifyMult("sw_blackhole" , 0f);
			Ship.getMutableStats().getMaxSpeed().modifyMult("sw_blackhole" , 0f);
		}
		else{
			Ship.getMutableStats().getMaxTurnRate().modifyMult("sw_blackhole" , 1f);
			Ship.getMutableStats().getMaxSpeed().modifyMult("sw_blackhole" , 1f);
		}
		
		
        if (beam.getWeapon().getChargeLevel() == 1f){
			beam.getDamage().setDamage(Damage * 4.5f);
            beam.setWidth(Width);
			fired = true;
			
			if (!Implode){
				Implode = true;
				
				//Setting values
				
				point.setX(beam.getTo().getX());
				point.setY(beam.getTo().getY());
				Map lightCrafts = new HashMap();
				List neighbours = new ArrayList();
				
				//Apply Instant Damage
				for (ShipAPI target : CombatUtils.getShipsWithinRange(point, BLACKHOLE_RADIUS)){
					for (int i = 0; i < 5; i++){
						engine.spawnEmpArc(beam.getWeapon().getShip(), point, target, target,
							DamageType.FRAGMENTATION,
							(IMPLODE_DAMAGE - (IMPLODE_DAMAGE * (MathUtils.getDistance(target , point) / BLACKHOLE_RADIUS))) / 5f,   //Damage is distributed across 5 instances and is dependant on proximity
							0f, // emp 
							100000f, // max range 
							null,
							0f, // thickness
							EMPTY,
							EMPTY);
					}	
				}
					
				for(ShipAPI s : CombatUtils.getShipsWithinRange(point, BLACKHOLE_RADIUS)){
					if(s.getCollisionClass()!=null)
						neighbours.add(s);
            
					//make fighters collide with everything.            
					if (s.isDrone() || s.isFighter()){
						lightCrafts.put(s, s.getCollisionClass());
						s.setCollisionClass(CollisionClass.ASTEROID);
						neighbours.add(s);
					}
				}
				
				//Generating BlackHole
				SW_BlackHolePlugin.addSingularity(new Vector2f(point), power * 0.28f, neighbours, lightCrafts, beam.getSource());
				Vector2f size = (Vector2f)(new Vector2f(SIZE)).scale(0.5f + power/20f);
        
				MagicRender.battlespace(
					Global.getSettings().getSprite("fx", "SW_Blackhole"),
					point,
					new Vector2f(),
					size,
					new Vector2f(),
					MathUtils.getRandomNumberInRange(0, 360),
					20,
					new Color(255,255,255,70),
					true,
					0.1f,
					2*power/5,
					3*power/5
				);
				MagicRender.battlespace(
					Global.getSettings().getSprite("fx", "SW_Blackhole"),
					point,
					new Vector2f(),
					(Vector2f)(new Vector2f(size)).scale(0.9f),
					new Vector2f(),
					MathUtils.getRandomNumberInRange(0, 360),
					40,
					new Color(255,255,255,60),
					true,
					power/5,
					2*power/5,
					2*power/5
				);
				MagicRender.battlespace(
					Global.getSettings().getSprite("fx", "SW_Blackhole"),
					point,
					new Vector2f(),
					(Vector2f)(new Vector2f(size)).scale(0.8f),
					new Vector2f(),
					MathUtils.getRandomNumberInRange(0, 360),
					57,
					new Color(255,255,255,50),
					true,
					2*power/5,
					2*power/5,
					power/5
				);
				MagicRender.battlespace(
					Global.getSettings().getSprite("fx", "SW_Blackhole"),
					point,
					new Vector2f(),
					(Vector2f)(new Vector2f(size)).scale(0.7f),
					new Vector2f(),
					MathUtils.getRandomNumberInRange(0, 360),
					80,
					new Color(255,255,255,40),
					true,
					3*power/5,
					power/5,
					power/5
				);

				//Sound + visual effects
				engine.spawnExplosion(point, (Vector2f) new Vector2f(0,0), EXPLOSION_COLOR, 40f * power, 1f);
				Global.getSoundPlayer().playUISound("Blackhole_Implode", 1f, 1f);
			}
		}
		else if (beam.getWeapon().getChargeLevel() < 1f && !fired){
			beam.getDamage().setDamage(Damage / 3f);
			beam.setWidth(Width / 2.5f);
		}
		else{
			Width2 = Width2 - 1f;
			beam.getDamage().setDamage(Damage / 3f);
			beam.setWidth(Math.max(Width2,Width/2.5f));
		}
	}
}

