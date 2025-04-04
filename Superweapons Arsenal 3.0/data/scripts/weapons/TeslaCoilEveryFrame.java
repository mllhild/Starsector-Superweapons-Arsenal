package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import org.lazywizard.lazylib.combat.CombatUtils;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.GameState;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.awt.Color;
import java.util.List;
import java.lang.Math;
import java.util.*;

public class TeslaCoilEveryFrame implements EveryFrameWeaponEffectPlugin {
	
	private static final Color COLOR = new Color(170, 180, 250, 150);
	private List<ShipAPI> TARGETS = new ArrayList();
	private List<MissileAPI> MISSILES = new ArrayList();
	private List<ShipAPI> FIGHTERS = new ArrayList();
	private static final float SEARCH_RANGE = 600f;
	private static final float ARC_DAMAGE = 100f;
	float counter = 0f;
	
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

        if (engine.isPaused()) 
            return;
		
		ShipAPI ship = weapon.getShip();
		ship.getVariant().addMod("sw_tesla_coil");
		
		if (Global.getSettings().getCurrentState() == GameState.COMBAT){
			counter += amount;
			
			Vector2f point = weapon.getLocation();
			
			if (weapon.isDisabled())
				weapon.repair();
		
			if (counter > 0.3f && !ship.getFluxTracker().isOverloaded()){
				counter = 0f;
				
				MISSILES.clear();
				FIGHTERS.clear();
				TARGETS.clear();
				
				//Scan for nearby ships
				for (ShipAPI shipx : CombatUtils.getShipsWithinRange(point, SEARCH_RANGE))
					if (shipx != ship && shipx.isAlive() && !ship.isAlly() && shipx.getOriginalOwner() != ship.getOriginalOwner())
						if (shipx.isFighter())
							FIGHTERS.add(shipx);
						else
							TARGETS.add(shipx);
				
				for (MissileAPI missile : CombatUtils.getMissilesWithinRange(point, SEARCH_RANGE))
					if (missile.getSource() != null)
					if (missile.getSource() != ship && !missile.getSource().isAlly() && missile.getSource().getOriginalOwner() != ship.getOriginalOwner())
						MISSILES.add(missile);
					
				//Randomization
				Collections.shuffle(MISSILES);
				Collections.shuffle(FIGHTERS);
				Collections.shuffle(TARGETS);
				
				//Priority order (Missiles -> Fighters -> Other ships)
				for (int i = 0; i < 4; i++){
					
					if (i < MISSILES.size())
						engine.spawnEmpArc(ship, point, (MissileAPI)MISSILES.get(i), (MissileAPI)MISSILES.get(i),
							DamageType.ENERGY,
							ARC_DAMAGE,
							0f,
							100000f,
							"BFG_Shock",
							30f,
							COLOR,
							COLOR.brighter());
					else if (i < FIGHTERS.size())
						engine.spawnEmpArc(ship, point, (ShipAPI) FIGHTERS.get(i), (ShipAPI) FIGHTERS.get(i),
							DamageType.ENERGY,
							ARC_DAMAGE,
							0f,
							100000f,
							"BFG_Shock",
							30f,
							COLOR,
							COLOR.brighter());
					else if (i < TARGETS.size())
						engine.spawnEmpArc(ship, point, (ShipAPI) TARGETS.get(i), (ShipAPI) TARGETS.get(i),
							DamageType.ENERGY,
							ARC_DAMAGE,
							0f,
							100000f,
							"BFG_Shock",
							30f,
							COLOR,
							COLOR.brighter());
						
				}
			}
		}
    }
}


