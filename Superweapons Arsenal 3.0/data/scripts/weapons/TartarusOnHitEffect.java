package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;

public class TartarusOnHitEffect implements OnHitEffectPlugin {

	private static final float SPLASH_RANGE = 400f;

    private static final Color COLOR1 = new Color(25, 115, 175);
    private static final Color COLOR2 = new Color(175, 205, 255);
    private static final Vector2f ZERO = new Vector2f();

	List<ShipAPI> SPLASH_TARGETS = new ArrayList();

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
		
		float DAMAGE = projectile.getDamageAmount() * 0.06f;
		float EMP = DAMAGE * 2f;

        for (ShipAPI Starget : CombatUtils.getShipsWithinRange(point, SPLASH_RANGE))
				SPLASH_TARGETS.add(Starget);
			
		for (ShipAPI Starget : SPLASH_TARGETS) 
			for (int i = 0; i < 5; i++)
				engine.spawnEmpArc(projectile.getSource(), point, Starget, Starget,
				DamageType.ENERGY,
				DAMAGE,
				EMP,
				100000f,
				"Tartarus_Explode",
				0f,
				COLOR2,
				COLOR2);
		
		float Incr = 0f;
		
        for (int i = 0; i < 17; i++) {
			
			Incr += 21.17f;
			float Offset = 350f + (float) Math.random() * 100f;
			
            Vector2f Loc = new Vector2f(projectile.getLocation().x + Offset, projectile.getLocation().y);
            Loc = VectorUtils.rotateAroundPivot(Loc, projectile.getLocation(), Incr, Loc);
			
            engine.spawnEmpArc(projectile.getSource(), point, null, new SimpleEntity(Loc), 
			DamageType.ENERGY, 
			0f,
            0f, 
			100000f,
            "Tartarus_Explode",
			10f,
			COLOR1,
			COLOR2);
        }
		
        Global.getSoundPlayer().playSound("Tartarus_Explode", 1f, 1f, point, ZERO);
    }
}
