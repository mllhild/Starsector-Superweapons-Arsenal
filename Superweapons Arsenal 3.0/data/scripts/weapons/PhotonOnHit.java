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
import com.fs.starfarer.api.combat.MissileAPI;
import org.lwjgl.util.vector.Vector2f;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;

public class PhotonOnHit implements OnHitEffectPlugin {

    private static final Color COLOR1 = new Color(115, 100, 205, 70);
    private static final Color COLOR2 = new Color(130, 120, 200, 145);
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
		
		if (target instanceof ShipAPI && shieldHit){
			ShipAPI Ship = (ShipAPI) target;
			Ship.getMutableStats().getDynamic().getStat("SW_PHOTON_SHIELD").modifyFlat("SW_PS2" , 0.1f);
		}
		else if (target instanceof ShipAPI && !shieldHit){
			ShipAPI Ship = (ShipAPI) target;
			Ship.getMutableStats().getDynamic().getStat("SW_PHOTON").modifyFlat("SW_P2" , 0.1f);
		}
		if (!(target instanceof MissileAPI)){
			engine.spawnExplosion(point, ZERO, COLOR1, 420f, 0.65f);
			engine.spawnExplosion(point, ZERO, COLOR2, 350f, 0.45f);
			Global.getSoundPlayer().playSound("Photon_Explode", 1f, 1f, point, ZERO);
		}
    }
}
