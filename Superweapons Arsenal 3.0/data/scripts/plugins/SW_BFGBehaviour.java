package data.scripts.plugins;

import com.fs.starfarer.api.Global;

import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import org.lazywizard.lazylib.combat.CombatUtils;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.MathUtils;
import data.scripts.util.MagicFakeBeam;
import org.lwjgl.util.vector.Vector2f;

import java.util.List;
import java.awt.Color;

public class SW_BFGBehaviour extends BaseEveryFrameCombatPlugin {
	
	private static final Color COLOR = new Color(70, 200, 70, 150);
	private static final float SEARCH_RANGE = 1000f;
	float Counter = 0f;
		
    @Override
    public void advance(float amount, List events) {
		CombatEngineAPI engine = Global.getCombatEngine();
		if (engine.isPaused()) {return;}
		
		Counter = Counter + amount;
		
		if (Counter > 0.166f){
			Counter = 0f;
			
			for (DamagingProjectileAPI proj : engine.getProjectiles()){
				if (proj.getWeapon() != null)
				if (proj.getWeapon().getSpec().hasTag("sw_bfg")){
					
					Vector2f point = proj.getLocation();
					WeaponAPI weapon = proj.getWeapon();
					float damage = weapon.getDamage().getDamage() / 6.6f;		//Base Shock damage is 15%
						
					if (!proj.didDamage() && !proj.isFading() && !proj.isExpired()){
						for (ShipAPI test : CombatUtils.getShipsWithinRange(point, SEARCH_RANGE)){
							if (test != weapon.getShip() && test.isAlive() && !test.isAlly() && weapon.getShip().getOriginalOwner() != test.getOriginalOwner()){
								
								if (!test.isStationModule() && !test.isFighter()){	//Normal ships
									MagicFakeBeam.spawnFakeBeam(
										engine, 														//CombatEngineAPI
										point, 															//from
										MathUtils.getDistance(point,test.getLocation()), 				//range
										VectorUtils.getAngle(point,test.getLocation()),					//Angle
										80f, 															//Width
										0f, 															//duration
										0.4f, 															//fade-out duration
										25f, 															//Impactsize
										COLOR.brighter(), 												//Core color
										COLOR, 															//Fringe color
										damage / 6f, 													//Damage
										DamageType.ENERGY, 												//Damage Type
										damage / 60f, 													//EMP
										weapon.getShip()												//Ship Source
									);	
								}
								else if (!test.isFighter()){		//Stations
									MagicFakeBeam.spawnFakeBeam(
										engine, 														//CombatEngineAPI
										point, 															//from
										MathUtils.getDistance(point,test.getLocation()), 				//range
										VectorUtils.getAngle(point,test.getLocation()),					//Angle
										80f, 															//Width
										0f, 															//duration
										0.4f, 															//fade-out duration
										25f, 															//Impactsize
										COLOR.brighter(), 												//Core color
										COLOR, 															//Fringe color
										(damage / 6f) * 0.4f, //Damage to stations is lower				//Damage
										DamageType.ENERGY, 												//Damage Type
										damage / 60f, 													//EMP
										weapon.getShip()												//Ship Source
									);	
								}
								else {
									MagicFakeBeam.spawnFakeBeam(	//Fighters
										engine, 														//CombatEngineAPI
										point, 															//from
										MathUtils.getDistance(point,test.getLocation()), 				//range
										VectorUtils.getAngle(point,test.getLocation()),					//Angle
										40f, 															//Width
										0f, 															//duration
										0.4f, 															//fade-out duration
										25f, 															//Impactsize
										COLOR.brighter(), 												//Core color
										COLOR, 															//Fringe color
										(damage / 6f) * 0.25f, //Damage to Fighters is even lower		//Damage
										DamageType.ENERGY, 												//Damage Type
										(damage / 60f) * 0.25f, 										//EMP
										weapon.getShip()												//Ship Source
									);	
									
								}
							//Sound Effects
							Global.getSoundPlayer().playSound("BFG_Shock", 1f, 1f, point, new Vector2f());
							}
						}
					} 	
				}
			}
		}		
	}
}
