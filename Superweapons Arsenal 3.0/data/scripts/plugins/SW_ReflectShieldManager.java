package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.loading.ProjectileSpecAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import org.lazywizard.lazylib.combat.CombatUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//Please don't redistribute this script without my permission

public class SW_ReflectShieldManager extends BaseEveryFrameCombatPlugin {
	
	private List <CombatEntityAPI> ReflectedProjectiles = new ArrayList();
	
    @Override
    public void advance(float amount, List events) {
		CombatEngineAPI engine = Global.getCombatEngine();
		if (engine.isPaused()) {return;}
		
		for (ShipAPI Ship : Global.getCombatEngine().getShips()){
			if (Ship.getVariant().hasHullMod("sw_reflective_shield")){
				float Radius = Ship.getShield().getRadius();
				
				for (DamagingProjectileAPI Projectile : CombatUtils.getProjectilesWithinRange(Ship.getLocation(), Radius * 1.1f)){
					
																									//Projectile to be reflected shouldn't be:
																									///////////////////////////////////
					if (Projectile != null && Projectile.getLocation() != null){					// null						     //
						if (!ReflectedProjectiles.contains(Projectile) &&							// reflected before			     //
						Projectile.getSource() != Ship &&											// fired by the reflecting ship  //
						!(Projectile instanceof MissileAPI) &&										// A missile					 //
						Ship.getShield().isWithinArc(Projectile.getLocation()) &&					// Not within the shield arc     //
						Projectile.getWeapon() != null &&											// Has no valid base weapon      //
						Projectile.getCollisionClass() != CollisionClass.NONE &&					// Piercing projectile		     //
						!Projectile.getWeapon().isBeam()){											// Generated by a Beam		     //
																									///////////////////////////////////
																									
							if (Projectile.getWeapon().getSpec().getProjectileSpec() instanceof ProjectileSpecAPI){
								if (Objects.equals(((ProjectileSpecAPI) Projectile.getWeapon().getSpec().getProjectileSpec()).getId(), Projectile.getProjectileSpecId())){       //We will ONLY reflect primary projectiles and will ignore splinters
									if(Projectile.getSource().isStationModule()){
										if (Projectile.getSource().getParentStation() != null){
											if (Projectile.getSource().getParentStation() != Ship){
												ReflectedProjectiles.add(Projectile);
												DamagingProjectileAPI ReflectedProjectile = (DamagingProjectileAPI) engine.spawnProjectile(Ship, Projectile.getWeapon(), Projectile.getWeapon().getId(), Projectile.getLocation(), Projectile.getFacing() + 180f, null); 
												ReflectedProjectile.setDamageAmount(ReflectedProjectile.getDamageAmount() * 1.2f);
												ReflectedProjectiles.add(ReflectedProjectile);
											}
										}
										else{
											ReflectedProjectiles.add(Projectile);
											DamagingProjectileAPI ReflectedProjectile = (DamagingProjectileAPI) engine.spawnProjectile(Ship, Projectile.getWeapon(), Projectile.getWeapon().getId(), Projectile.getLocation(), Projectile.getFacing() + 180f, null); 
											ReflectedProjectile.setDamageAmount(ReflectedProjectile.getDamageAmount() * 1.2f);
											ReflectedProjectiles.add(ReflectedProjectile);
										}
									}
									else{
										ReflectedProjectiles.add(Projectile);
										DamagingProjectileAPI ReflectedProjectile = (DamagingProjectileAPI) engine.spawnProjectile(Ship, Projectile.getWeapon(), Projectile.getWeapon().getId(), Projectile.getLocation(), Projectile.getFacing() + 180f, null); 
										ReflectedProjectile.setDamageAmount(ReflectedProjectile.getDamageAmount() * 1.2f);
										ReflectedProjectiles.add(ReflectedProjectile);
									}
								}
							}
						}
					}
				}
			}
		}
	}
}

