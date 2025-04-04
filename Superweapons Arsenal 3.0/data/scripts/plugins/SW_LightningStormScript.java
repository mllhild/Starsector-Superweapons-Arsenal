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
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.awt.Color;
import java.util.List;
import java.lang.Math;
import java.util.*;

//This script handles Everything related to Lightning storms except turret effects

public class SW_LightningStormScript extends BaseEveryFrameCombatPlugin {
	
    private static final Color COLOR = new Color(130, 130, 250, 125);		//The more variables you define the smarter people will think you are.
	private static final float STORM_BASE_DURATION = 10f;
	private static final float STORM_BASE_RADIUS = 2000f;
	private static final float STORM_BASE_DAMAGE = 20000f;
	private static final float STRIKE_RANGE = 400f;
	private static final float SPLASH_RADIUS = 1200f;
	private static final float TRIGGER_RANGE = 200f;
	private static final float STORM_DELAY = 4f;
	private List<ShipAPI> TARGETS = new ArrayList<>();
	private float dynamicArcLength;
	
	//Define an array to store our storms
	private static final List<lightningStorm> STORMS = new ArrayList<>();
	
	float counter = 0f;
	
	//A class to define the components of a Storm
	private static class lightningStorm {
		private final ShipAPI SOURCE;	//Originating ship
		private final Vector2f LOC;		//Storm Location
		private float COILS;			//Coils used for the Storm
		private float TIME;				//Time elapsed
		private boolean A1;				//Audio Flag
		private boolean A2;				//Audio Flag
	//We need localized counters otherwise different storms are going to interfer with each other.
		private float C1;				//Counter
		private float C2;				//Counter
		private float C3;				//Counter
		
		
		public lightningStorm(ShipAPI source, Vector2f loc, float coils, float time, boolean a1, boolean a2, float c1, float c2, float c3) {
			this.SOURCE = source;
			this.LOC = loc;
			this.COILS = coils;
			this.TIME = time;
			this.A1 = a1;
			this.A2 = a2;
			this.C1 = c1;
			this.C2 = c2;
			this.C3 = c3;
        }
    }
	
	//We call this to generate a new Storm
	public static void CreateLightningStorm(ShipAPI source, Vector2f loc, float coils) {
        STORMS.add(new lightningStorm(source, loc, coils, 0f, false, false, 0f, 0f, 0f));
    }
	
    @Override
    public void advance(float amount, List events) {
		CombatEngineAPI engine = Global.getCombatEngine();
		if (engine.isPaused()) {return;}
		
		float Timer = Global.getCombatEngine().getTotalElapsedTime(false);
		counter += amount;
		
		if (counter > 0.2f){
			counter = 0f;
			for (DamagingProjectileAPI proj : engine.getProjectiles()){
			
				if (proj.getWeapon() != null)				//enclosure to avoid null pointer errors
				if (proj.getWeapon().getSpec() != null)		//enclosure to avoid null pointer errors
				if (proj.getWeapon().getSpec().hasTag("sw_lightning_storm")){
					
					if (!proj.isExpired() && proj.getElapsed() > 1f){
						
						boolean flag1 = false;
						float TeslaCoilAmount = 0f;
						Vector2f point = proj.getLocation();
						WeaponAPI weapon = proj.getWeapon();
						ShipAPI ship = weapon.getShip();
						float Damage = proj.getDamageAmount();
						
						
						//Check for nearby ships
						for (ShipAPI shipx : CombatUtils.getShipsWithinRange(point, TRIGGER_RANGE)){
							if (shipx != ship && shipx.isAlive() && !ship.isAlly() && shipx.getOriginalOwner() != ship.getOriginalOwner() && !shipx.isFighter())
								flag1 = true;
						}
						
						//Trigger at maximum range
						if (proj.isFading())
							flag1 = true;	
						
						
						if (flag1){
							
							//Get the number of coils used to fire the Lightning Storm Generator
							for (WeaponAPI weapons : ship.getAllWeapons()){
								if (weapons.getSpec().hasTag("sw_tesla_coil"))
									TeslaCoilAmount++;							
							}
							
							RippleDistortion ripple = new RippleDistortion(point, new Vector2f());
								ripple.setSize(SPLASH_RADIUS);
								ripple.setIntensity(300f);
								ripple.setFrameRate(30f);
								ripple.fadeInSize(0.7f);
								ripple.fadeOutIntensity(0.5f);
							
							//Create the storm
							CreateLightningStorm(ship, point, Math.min(4f,TeslaCoilAmount));
							
							//Deal some damage, cuz why not.
							for (ShipAPI shipz : CombatUtils.getShipsWithinRange(point, SPLASH_RADIUS)){
								if (shipz.isAlive() && shipz != ship){
									int x = 6;
									if (!shipz.isFighter())
										x = 6;
									else
										x = 1;
									for (int i = 0; i < x; i++){
										engine.spawnEmpArc(ship, point , shipz, shipz, 
										DamageType.ENERGY, 
										Damage / 6f,
										300f, 
										100000f,
										null,
										60f,
										COLOR,
										COLOR.brighter());
									}
								}
							}
							
							//Visuals
							engine.spawnExplosion(point, new Vector2f(), COLOR, 1400f, 4f);
							engine.spawnExplosion(point, new Vector2f(), COLOR.brighter(), 405f, 0.8f);
							DistortionShader.addDistortion(ripple);
							Global.getSoundPlayer().playUISound("Lightning_Storm_Explode", 1f, 1f);
							
							float Offset = 500f;
							for (int f = 0; f < 20; f++){
								
								if (f < 7)
									Offset = 500f;
								else if (f < 14)
									Offset = 1000f;
								else
									Offset = 1500f;
							
								//Some 2D-Victor Wizardry
								float RotationAmount = 52f * f + (float) Math.random() * 22f;
								float RotationOffset = (float)(170f + Math.random() * 30f);
								Vector2f Loc = new Vector2f(point.x + Offset, point.y);
								Loc = VectorUtils.rotateAroundPivot(Loc, point, RotationAmount, Loc);
								Vector2f targetOffset = new Vector2f(Loc.x + RotationOffset, Loc.y);
								targetOffset = VectorUtils.rotateAroundPivot(targetOffset, Loc, 90f + (52f * f) , targetOffset);
				
								engine.spawnEmpArc(ship, Loc , null, new SimpleEntity(targetOffset), 
									DamageType.ENERGY, 
									0f,
									0f, 
									100000f,
									null,
									120f,
									COLOR,
									COLOR.brighter());
							}
							//Remove the static charge after triggering it.
							engine.removeEntity(proj);
						}
					}
				}
			}
		}
		
		if (Timer < 1f)	//delete lingering storms from any previous battle at the start of combat.	-Added with Path 2.5b
			STORMS.clear();
		
		//Storms are handled here
		for (lightningStorm Storm : STORMS){
			
			//Calculate the variables for every Lightning Storm based on the amount of coils used to generate it.
			float multiplier = 1f + Storm.COILS / 4f;
			
			float occuranceMultiplier = 0.1f / multiplier;										//Increased Strike rate instead of increasing damage (Armor-related reasons)
			float stormRadius = STORM_BASE_RADIUS * multiplier;									//Maximum storm radius is 4000.
			float stormDuration = STORM_BASE_DURATION * multiplier + STORM_DELAY;				//Maximum storm duration is 24 seconds (including the delay).
			float arcDamage = STORM_BASE_DAMAGE / 10f;											//The damage of the storm is spread over 10 arcs
			
			
			//We loop over every storm that didn't reach it's maximum duration
			if (Storm.TIME < stormDuration){
				Storm.TIME += amount;
				Storm.C1 += amount;
				Storm.C2 += amount;
				Storm.C3 += amount;
				
				//Lightning Damage
				if (Storm.TIME > STORM_DELAY){
					if (Storm.C1 > occuranceMultiplier){
						Storm.C1 = 0f;
						
						//How does it work ?
						//1_Each strike starts from a random anchor inside the storm
						//2_Search for a second random location inside the storm for the Arc to strike at
						//3_if the distance between both locations is higher than 800-1600 or lower than 400-800 redo the previous step
						//4_Once the distance is ideal, pick a random target around the second location and strike it, otherwise strike nothing
						
						
						Vector2f StrikeAnchor = new Vector2f();
						Vector2f StrikeArea = new Vector2f();
						float distance = 0f;
						
						float maximumAllowedDistance = 800f * multiplier;
						float minimumAllowedDistance = 400f * multiplier;
						
						while (distance < minimumAllowedDistance || distance > maximumAllowedDistance){
							StrikeAnchor = MathUtils.getRandomPointInCircle(Storm.LOC, stormRadius);
							StrikeArea = MathUtils.getRandomPointInCircle(Storm.LOC, stormRadius);
							distance = MathUtils.getDistance(StrikeAnchor,StrikeArea);
						}
						
						float arcThickness = 150f + (250f * (float)(Math.min(1f, distance/1600f)));
						
						for (ShipAPI target : CombatUtils.getShipsWithinRange(StrikeArea, 450f * multiplier)){
							if (target.isAlive() && !target.isFighter())
								TARGETS.add(target);
						}
						
						//Apply Damage
						if (TARGETS.size() > 0){
							Collections.shuffle(TARGETS);
							
							float TargetCoils = 0f;
						
							for (WeaponAPI weaponx : ((ShipAPI)TARGETS.get(0)).getAllWeapons())
								if (weaponx.getSpec().hasTag("sw_tesla_coil"))
									TargetCoils++;							
							
							//Reduce damage based on the number of Xatrix Coils the target has
							float finalDamage = arcDamage - (arcDamage * (Math.min(TargetCoils, 4f) * 0.15f));
							
							engine.spawnEmpArc(Storm.SOURCE, StrikeAnchor, (ShipAPI)TARGETS.get(0), (ShipAPI)TARGETS.get(0),
								DamageType.ENERGY,
								finalDamage,		 // damage
								finalDamage / 10f, 	 // emp 
								100000f,			 // max range 
								null,      			 // sound
								arcThickness,		 // thickness
								COLOR,
								COLOR.brighter());
									
							TARGETS.clear();	
						}
						else{
							
							engine.spawnEmpArc(Storm.SOURCE, StrikeAnchor , null, new SimpleEntity(StrikeArea), 
								DamageType.ENERGY, 
								0f,
								0f, 
								100000f,
								null,
								arcThickness,
								COLOR,
								COLOR.brighter());
						}
						
						//Arc Sound (30% chance per arc)
						float x = (float)Math.random();
						if (x > 0.7f)
							Global.getSoundPlayer().playSound("Lightning_Storm_Strike", 1f, 0.35f + (0.65f * (float)(Math.min(1f, distance/1600f))), StrikeArea, new Vector2f());
						
					}
				}
				
				
				//Passive lightning effects
				if (Storm.TIME < STORM_DELAY){
					if (Storm.C2 > occuranceMultiplier / 2f){
						Storm.C2 = 0f;
					
						dynamicArcLength = 40f + (float)(100f * Math.random());
						
						Vector2f StrikeLocation1 = MathUtils.getRandomPointInCircle(Storm.LOC, stormRadius);
						Vector2f StrikeLocation2 = MathUtils.getRandomPointOnCircumference(StrikeLocation1, dynamicArcLength);
					
						engine.spawnEmpArc(Storm.SOURCE, StrikeLocation1 , null, new SimpleEntity(StrikeLocation2), 
							DamageType.ENERGY, 
							0f,
							0f, 
							100000f,
							null,
							70f,
							COLOR,
							COLOR.brighter());
					}
				}
				else{
					if (Storm.C2 > occuranceMultiplier * 2f){
						Storm.C2 = 0f;
					
						dynamicArcLength = 60f + (float)(100f * Math.random());
						
						Vector2f StrikeLocation1 = MathUtils.getRandomPointInCircle(Storm.LOC, stormRadius);
						Vector2f StrikeLocation2 = MathUtils.getRandomPointOnCircumference(StrikeLocation1, dynamicArcLength);
					
						engine.spawnEmpArc(Storm.SOURCE, StrikeLocation1 , null, new SimpleEntity(StrikeLocation2), 
							DamageType.ENERGY, 
							0f,
							0f, 
							100000f,
							null,
							70f,
							COLOR,
							COLOR.brighter());
					}
					
					//Passive particle effects
					if (Storm.C3 > occuranceMultiplier){
						Storm.C3 = 0f;
					
						Vector2f particleLocation = MathUtils.getRandomPointInCircle (Storm.LOC, stormRadius * 1.2f);
					
						engine.addHitParticle(particleLocation, new Vector2f(), 500f, 1.5f, 1.5f, COLOR);
					}
				}
				
				
				////Audio////
				
				//Warning sound
				if (Storm.TIME > 1f && !Storm.A1){
					Storm.A1 = true;
					Global.getSoundPlayer().playUISound("Lightning_Storm_Warning", 1f, 0.8f);
				}
				
				//Storm Starting sound
				if (Storm.TIME > 4f && !Storm.A2){
					Storm.A2 = true;
					Global.getSoundPlayer().playUISound("Lightning_Storm_Start", 1f, 1f);
				}
				
				//Storm Loop sound
				if (Storm.TIME > 4){
					Global.getSoundPlayer().playUILoop("Lightning_Storm_Loop", 1f, 1f);
				}
			}
		}
	}
}
