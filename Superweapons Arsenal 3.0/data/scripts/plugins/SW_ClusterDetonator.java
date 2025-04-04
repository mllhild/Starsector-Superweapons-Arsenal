package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import org.dark.shaders.distortion.RippleDistortion;
import org.dark.shaders.distortion.DistortionShader;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import org.lazywizard.lazylib.combat.CombatUtils;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.awt.Color;
import java.util.List;
import java.lang.Math;
import java.util.*;

public class SW_ClusterDetonator extends BaseEveryFrameCombatPlugin {
	
	private static final float SEARCH_RANGE = 400f;
	private List <MissileAPI> Mines = new ArrayList();
	private float Counter = 0f;
	private static final Color COLOR1 = new Color(100, 100, 225, 95);
    private static final Color COLOR2 = new Color(100, 100, 250, 115);
    private static final Color COLOR3 = new Color(220, 120, 40, 70);
    private static final Color COLOR4 = new Color(255, 180, 60, 125);
    private static final Vector2f ZERO = new Vector2f();
	private float AoE = 750f;
	private float TIMER = 0f;
	float INTERVAL = 0f;
	
    @Override
    public void advance(float amount, List events) {
		CombatEngineAPI engine = Global.getCombatEngine();
		TIMER += amount;
		
		if (engine.isPaused()) {return;}
		
		
		//Get mines
		for(MissileAPI p : engine.getMissiles())
			if (!Mines.contains(p) && p.getMaxFlightTime() == 35.5f && p.getHitpoints() > 0f)
					Mines.add(p);
		Collections.shuffle(Mines);		
		
		if (!Mines.isEmpty())
			Counter += amount;

		for (MissileAPI Mine : Mines){
			Vector2f point = Mine.getLocation();
			boolean Detonated = Mine.isExpired() || Mine.getHitpoints() <= 0f;
			
			//lightning effects
			if (!Detonated){
				if (Counter > 0f && Counter < 0.5f)
					Mine.setGlowRadius(40f);
				else
					Mine.setGlowRadius(0f);
			}
			
			//Detonate mines
			if (TIMER > INTERVAL && Mine.getElapsed() > 10f && !Detonated){
				
				//Detonation interval ranges between 0.2 and 0.4 second
				INTERVAL = 0.2f + (0.2f * (float) Math.random());
				
				TIMER = 0f;
				
				engine.spawnEmpArc(Mine.getSource(), point, Mine, Mine,
					DamageType.ENERGY,
					150000,
					0f, // emp 
					100000f, // max range 
					"Photon_Explode",
					0f, // thickness
					COLOR1,
					COLOR1);
		
				RippleDistortion ripple = new RippleDistortion(Mine.getLocation(), ZERO);
					ripple.setSize(1000f);
					ripple.setIntensity(200f);
					ripple.setFrameRate(30f);
					ripple.fadeInSize(0.8f);
					ripple.fadeOutIntensity(0.5f);
					
					
				for (ShipAPI target : CombatUtils.getShipsWithinRange(point, AoE)){
					float Damage = 6000f + (9000f * ((AoE - MathUtils.getDistance(target , point)) / AoE)); //Base Damage is 6000 and grows by an additional 9000 based on Proximity.
					Damage = Damage / 3f; //Damage will be dealt over 3 instances.
					
					if (!target.isStationModule())
					for (int i = 0; i <3; i++)
						engine.spawnEmpArc(Mine.getSource(), point, target, target,
							DamageType.KINETIC,
							Damage,
							0f, // emp 
							100000f, // max range 
							"Photon_Explode",
							0f, // thickness
							COLOR1,
							COLOR1);
					else
					for (int i = 0; i <3; i++)
						engine.spawnEmpArc(Mine.getSource(), point, target, target,
							DamageType.KINETIC,
							Damage * 0.5f, 		//Only 50% of damage is dealt to stations and other modules.
							0f, // emp 
							100000f, // max range 
							"Photon_Explode",
							0f, // thickness
							COLOR1,
							COLOR1);
				}
				
				//Aesthetic
				engine.spawnExplosion(point, ZERO, COLOR1, 1700f, 2f);
				engine.spawnExplosion(point, ZERO, COLOR2, 900f, 0.8f);
				Global.getSoundPlayer().playUISound("Photon_Explode", 1f, 1f);
				DistortionShader.addDistortion(ripple);
				
			}
		}
		if (Counter > 1f)
			Counter = 0f;
	
	
	
	
	
	
	
	
	
		//Supernova handler (i put it here due to similar functionality)
	
		for(MissileAPI Nova : engine.getMissiles()){
			if (Nova.getWeapon() != null && Nova.getWeapon().getSpec() != null)
			if (Nova.getHitpoints() > 0f && Nova.getWeapon().getSpec().hasTag("supernova") && Nova.getElapsed() > 1f){
				
				Vector2f point2 = Nova.getLocation();
				boolean Empty = true;
				
				RippleDistortion ripple = new RippleDistortion(point2, ZERO);
					ripple.setSize(1000f);
					ripple.setIntensity(200f);
					ripple.setFrameRate(30f);
					ripple.fadeInSize(0.8f);
					ripple.fadeOutIntensity(0.5f);
				
				
				//Proximity search:
				for (ShipAPI Target : CombatUtils.getShipsWithinRange(point2, SEARCH_RANGE))
					if (Target.isAlive() && !Target.isFighter() && !Target.isAlly() && Target.getOriginalOwner() != Nova.getSource().getOriginalOwner())
						Empty = false;
				
				
				if (!Empty){
					
					//kill self
					engine.applyDamage(Nova , point2, 15000, DamageType.FRAGMENTATION, 0f, false, false, Nova.getSource(), true);
					
					//split
					for (float x = 0f; x < 20f; x++){
						engine.spawnProjectile(null, null, "sw_phalanx_single", point2 , Nova.getFacing() + 18f * x , null);
					}
					
					//Aesthetic
					engine.spawnExplosion(point2, ZERO, COLOR3, 1000f, 1.5f);
					engine.spawnExplosion(point2, ZERO, COLOR4, 650f, 0.7f);
					Global.getSoundPlayer().playUISound("Supernova_Explode", 1f, 1f);
					DistortionShader.addDistortion(ripple);
					
					for (int f = 0; f < 6; f++){
				
						float Offset = 300f;
						float RotationAmount = 60 * f + (float) Math.random() * 25f;
				
						Vector2f Loc = new Vector2f(point2.x + Offset, point2.y);
						Loc = VectorUtils.rotateAroundPivot(Loc, point2, RotationAmount, Loc);
				
						Vector2f targetOffset = MathUtils.getRandomPointOnCircumference(Loc, 130f);
				
						engine.spawnEmpArc(Nova.getSource(), Loc , null, new SimpleEntity(targetOffset), 
							DamageType.ENERGY, 
							0f,
							0f, 
							100000f,
							null,
							50f,
							COLOR2,
							COLOR2.brighter());
					}
				}
			}
		}
	}
}

