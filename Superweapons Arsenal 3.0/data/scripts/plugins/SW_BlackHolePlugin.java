package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.util.MagicRender;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;

public class SW_BlackHolePlugin extends BaseEveryFrameCombatPlugin {

    private final Color CORE = new Color(120,100,255), FRINGE =new Color(150,70,250), BLANK =new Color(0,0,0);
	private final IntervalUtil singularityTime= new IntervalUtil(0.05f,0.05f);
	private static final List<singularityData2> BLACKHOLES = new ArrayList();
	private final IntervalUtil globalTimer=new IntervalUtil(0.05f,0.05f);
    private static final Vector2f ZERO = new Vector2f();
	private final String slow_ID = "SW_BlackHoleEffect";
	private final float DAMAGE = 500f;
	private final float NEWTON = 2f;
	
	//////////////////////////////////
    //                         		//
	//	 Tartiflette Singularity	//
    //   Plugin, Authorized to use	//
	//   by Him <3					//
	//								//
    //////////////////////////////////

    private static class singularityData2 {
        private final Vector2f LOC;
        private float TIME;
        private final List<ShipAPI> AFFECTED;
        private final Map<CombatEntityAPI,CollisionClass> COLLISIONS;
        private final ShipAPI SOURCE;
		
        public singularityData2(Vector2f loc, float time, List affected, Map collisions, ShipAPI source) {
            this.LOC = loc;
            this.TIME = time;
            this.AFFECTED = affected;
            this.COLLISIONS = collisions;
            this.SOURCE = source;
        }
    }
	
	public void init(CombatEngineAPI engine) {
        BLACKHOLES.clear();
	}
    
    public static void cleanSlate(){
        BLACKHOLES.clear();
    }
	
	public void advance(float amount, List events) {
		
		CombatEngineAPI engine = Global.getCombatEngine();
        if (engine.isPaused()) 
            return;
		
		for (singularityData2 singularity : BLACKHOLES) {
			if (singularity.TIME > 0)
				//Black Loop sound
				Global.getSoundPlayer().playLoop("Blackhole_Loop", engine, 1f, 0.9f, singularity.LOC, ZERO);
		}
		
        globalTimer.advance(amount);
        if (globalTimer.intervalElapsed()) {
			if(!BLACKHOLES.isEmpty()){
				applySingularityEffect(amount, engine);
			}
		}
	}

    public static void addSingularity(Vector2f location, float timeLeft, List affectedList, Map collisionsList, ShipAPI source) {
        BLACKHOLES.add(new singularityData2(location, timeLeft, affectedList, collisionsList, source));
    }

    private void applySingularityEffect(float amount, CombatEngineAPI engine){
        
        singularityTime.advance(amount);
		
        if(singularityTime.intervalElapsed()){
        
            for (Iterator iter = BLACKHOLES.iterator(); iter.hasNext();) {
                singularityData2 singularity = (singularityData2) iter.next();

                singularity.TIME-=Math.max(0.05f, amount);

                // Check if the singularity is gone
                if (singularity.TIME < 0) {
					Global.getSoundPlayer().playUISound("Blackhole_Stabilize", 1f, 0.9f);
					
                    //unapply the slowing effect
                    for(ShipAPI s : singularity.AFFECTED){
                        s.getMutableStats().getAcceleration().unmodify(slow_ID);
                        s.getMutableStats().getTurnAcceleration().unmodify(slow_ID);
                    }                 
                    //restore the original collision class
                    Map <CombatEntityAPI,CollisionClass> theCollisions = singularity.COLLISIONS;
                    for (CombatEntityAPI object : theCollisions.keySet()) {
                        if(engine.isEntityInPlay(object) && object.getCollisionClass()==CollisionClass.ASTEROID){
                            object.setCollisionClass((CollisionClass) theCollisions.get(object));                            
                        }
                    }                    
                    //remove the whole thing
                    iter.remove();
                } else {
					
                    // Apply force to nearby entities
                    
                    float power = Math.min(2,singularity.TIME) / 2f;
                    
                    List <CombatEntityAPI> pull = CombatUtils.getEntitiesWithinRange(singularity.LOC, 1800f);
                    if (pull != null) {                        
                        float force;
                        
                        for (CombatEntityAPI tmp : pull) {
                            //skip module parts and fixed stations
                            if(tmp instanceof ShipAPI &&(((ShipAPI)tmp).getParentStation()!= null && ((ShipAPI)tmp).getVariant().getHullMods().contains("axialrotation"))){
                                continue;
                            }
                            
                            float distance = MathUtils.getDistanceSquared(tmp.getLocation(), singularity.LOC);
							
                            float angle = VectorUtils.getAngle( tmp.getLocation(),singularity.LOC )-1-(15*(1000000-distance)/1000000); //do not pull exactly to the center, avoiding collisions penetrations and make them swirl micely
                            
                            if (distance >= 2500) {
                               force = (Math.min(2,singularity.TIME)) * Math.min(1, power)* NEWTON ; //power x distance falloff, not a realistic square of the distance drop for gameplay purpose                                 

                                if(tmp instanceof ShipAPI){
                                    ShipAPI ship = (ShipAPI)tmp;
									
									
									//Apply continuous damage
									engine.spawnEmpArcPierceShields(singularity.SOURCE, singularity.LOC, ship, ship,
										DamageType.FRAGMENTATION,
										DAMAGE - (DAMAGE * (MathUtils.getDistance(ship, singularity.LOC) / 2000f)),
										0f, // emp 
										100000f, // max range 
										null,
										0f, // thickness
										BLANK,
										BLANK);
										
									
                                    Vector2f sVel = MathUtils.getPoint(new Vector2f(), force * 12, angle);
                                    Color color=new Color(power/9,power/10,power/7,power/10);
                                    ship.addAfterimage(
                                            color,
                                            0,      
                                            0,      
                                            sVel.x,
                                            sVel.y,
                                            2f,
                                            0.1f,
                                            0.2f,
                                            0.3f,
                                            false,
                                            true,
                                            false
                                    );
                                }
                                
                                if(tmp instanceof MissileAPI && Math.random()<0.25){
                                    //Missiles can hit allies
                                    if(Math.random()<(0.25-(distance/2000000))){
                                        if(((MissileAPI)tmp).getCollisionClass() == CollisionClass.MISSILE_NO_FF){
                                            ((MissileAPI)tmp).setCollisionClass(CollisionClass.MISSILE_FF);
                                        }
                                    }
                                } 
                                
                                if(tmp instanceof DamagingProjectileAPI){
                                    force*=15;
                                }

                                //MANUAL MASS-FREE FORCE    
                                Vector2f direction = MathUtils.getPoint(new Vector2f(),1f, angle);       
                                force*=Math.max(
                                        .5f,
                                        (Math.abs(MathUtils.getShortestRotation(
                                                angle,
                                                VectorUtils.getFacing(tmp.getVelocity())
                                        )))/90
                                );
                                direction.scale(force);
                                Vector2f.add(direction, tmp.getVelocity(), tmp.getVelocity());
                                
                                if(tmp instanceof DamagingProjectileAPI){
                                    tmp.setFacing(VectorUtils.getFacing(tmp.getVelocity()));
                                }
                           }      
                        }
                    }
                    
                    // disrupt the engines of the ships affected by the initial blast                    
                    for(ShipAPI s : singularity.AFFECTED){
                        s.getMutableStats().getAcceleration().modifyMult(slow_ID, Math.max(0.5f, (2-power)/2));
                        s.getMutableStats().getTurnAcceleration().modifyMult(slow_ID, Math.max(0.5f, (2-power)/2));
                    }
					
					////////////////////////////////
					// VISUAL EFFECTS AHEAD! ///////
					////////////////////////////////
					
                    if(MagicRender.screenCheck(0.25f, singularity.LOC)){
                        //GLOW!
                        engine.addSmoothParticle(
                                singularity.LOC,
                                new Vector2f(),
                                200f,
                                1,
                                0.5f+1f*(float)Math.random(),
                                FRINGE
                        );

                        engine.addHitParticle(
                                singularity.LOC,
                                new Vector2f(),
                                25*power+50*(float)Math.random(),
                                1,
                                0.25f+0.5f*(float)Math.random(),
                                CORE
                        );

                        //PARTICLES!
                        for(int i=0; i<Math.round(power*3); i++){
                            float radius = 200f + (float)Math.random() * 1600f;
                            Color particles = new Color(0.4f+0.3f*(2000-radius)/2000,0.1f+0.1f*(2000-radius)/2000,1f);

                            //public void addSmoothParticle(Vector2f loc, Vector2f vel, float size, float brightness, float duration, Color color)
							for (int a = 0; a < 8; a++){
								radius = 200f + (float)Math.random() * 1600f;
								for (int s = 0; s < 8; s++){
									float angle = (float)Math.random()*360;
									float azimut = angle + 180 - 90*(radius/1500);
									engine.addSmoothParticle(
										MathUtils.getPoint(singularity.LOC, radius, angle),
										MathUtils.getPoint(new Vector2f(), 50+(2000-radius)/6, azimut),
										15f,
										1,
										radius/1000,
										particles
									);
								}
							}
						}

                        //FLARE!
                        if(Math.random()<0.25){
                            float spread=(float)Math.random()*2;
                            Vector2f offset=new Vector2f((float)Math.random()*10,(float)Math.random()*5);
                            engine.spawnEmpArc(
                                    singularity.SOURCE,
                                    new Vector2f(singularity.LOC.x+offset.x, singularity.LOC.y-spread*power+offset.y),
                                    null,
                                    new SimpleEntity(new Vector2f(singularity.LOC.x+offset.x, singularity.LOC.y+spread*power+offset.y)),
                                    DamageType.KINETIC,
                                    0,
                                    0,
                                    20000,
                                    null,
                                    400+300*power+(float)Math.random()*100,
                                    FRINGE,
                                    CORE
                            );
                        }

                        //ARCS!
                        if(Math.random()<Math.min(singularity.TIME/10, 0.2f)){
                            //public CombatEntityAPI spawnEmpArc(ShipAPI damageSource, Vector2f point, CombatEntityAPI pointAnchor, CombatEntityAPI empTargetEntity, DamageType damageType, float damAmount, float empDamAmount, float maxRange, String impactSoundId, float thickness, Color fringe, Color core)
                            engine.spawnEmpArc(
                                    singularity.SOURCE,
                                    singularity.LOC,
                                    null,
                                    new SimpleEntity(MathUtils.getRandomPointInCircle(singularity.LOC, 1500f)),
                                    DamageType.KINETIC, 
                                    0, 
                                    0, 
                                    2000,
                                    null,
                                    20f,
                                    FRINGE,
                                    CORE
                            );
                        }   
                    }
                }
            }
        }   
    }
}
