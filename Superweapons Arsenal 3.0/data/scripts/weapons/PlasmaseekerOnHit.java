package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import org.lwjgl.util.vector.Vector2f;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.ShipAPI;
import org.lazywizard.lazylib.combat.CombatUtils;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;



public class PlasmaseekerOnHit implements OnHitEffectPlugin {
	
	private static final float SPLASH_RANGE = 125f;
	List<ShipAPI> SPLASH_TARGETS = new ArrayList();
    private static final Color COLOR1 = new Color(135, 100, 220, 70);
    private static final Color COLOR2 = new Color(180, 150, 255, 125);
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
		float DAMAGE = projectile.getDamageAmount();
		
		for (ShipAPI Starget : CombatUtils.getShipsWithinRange(point, SPLASH_RANGE))
			if (Starget != target && !Starget.isStationModule())
				SPLASH_TARGETS.add(Starget);
		
		for (ShipAPI Starget : SPLASH_TARGETS) 
			engine.spawnEmpArc(projectile.getSource(), point, Starget, Starget,
			DamageType.ENERGY,
			DAMAGE,
			0f, // emp 
			100000f, // max range 
			"Plasma_Explode",
			0f, // thickness
			COLOR1,
			COLOR1);
			
		engine.spawnExplosion(point, ZERO, COLOR1, 200f, 1.3f);
		engine.spawnExplosion(point, ZERO, COLOR2, 150f, 0.6f);
		Global.getSoundPlayer().playSound("Plasma_Explode", 1f, 0.7f, point , ZERO);
	}
}
