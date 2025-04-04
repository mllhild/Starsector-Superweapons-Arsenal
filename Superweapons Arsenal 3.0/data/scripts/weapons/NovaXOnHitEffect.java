package data.scripts.weapons;

import java.util.ArrayList;
import java.util.List;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;
import org.lazywizard.lazylib.combat.CombatUtils;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;

public class NovaXOnHitEffect implements OnHitEffectPlugin {

    private static final float SPLASH_RANGE = 250f;
    private static final Color COLOR1 = new Color(150, 100, 70, 70);
    private static final Color COLOR2 = new Color(185, 150, 110, 125);
    private static final Color NULL = new Color(0, 0, 0, 0);
    private static final Vector2f ZERO = new Vector2f();


    public void onHit(DamagingProjectileAPI projectile,
            CombatEntityAPI target,
            Vector2f point,
            boolean shieldHit,
            ApplyDamageResultAPI damageResult, 
            CombatEngineAPI engine)
	{
        if (target == null || point == null) {
            return;
        }
		
		float Damage = projectile.getDamageAmount();
		
		for (ShipAPI Starget : CombatUtils.getShipsWithinRange(point, SPLASH_RANGE))
			if (Starget != target && !Starget.isAlly() && Starget.getOriginalOwner() != projectile.getWeapon().getShip().getOriginalOwner() && !Starget.isStationModule()){
				for (int i = 0; i < 3; i++){
					engine.spawnEmpArc(projectile.getSource(), point, Starget, Starget,
						DamageType.FRAGMENTATION,
						Damage /3f,
						0f, // emp 
						100000f, // max range 
						null,
						0f, // thickness
						NULL,
						NULL);
					
				}
			}
			
		for (MissileAPI Missile : CombatUtils.getMissilesWithinRange(point, SPLASH_RANGE))
			if (Missile.getWeapon() != null){
			if (Missile.getWeapon().getSpec() != null)
			if (!Missile.getWeapon().getSpec().hasTag("sw_novax"))
				engine.applyDamage(Missile , point, Damage, DamageType.FRAGMENTATION, 0f, false, false, projectile.getSource(), true);
			}
			else{
				engine.applyDamage(Missile , point, Damage, DamageType.FRAGMENTATION, 0f, false, false, projectile.getSource(), true);
			}
		
		engine.spawnExplosion(point, ZERO, COLOR1, 400f, 1f);
		engine.spawnExplosion(point, ZERO, COLOR2, 130f, 0.4f);
		Global.getSoundPlayer().playSound("Photon_Explode", 1f, 0.75f, point, ZERO);
    }
}
