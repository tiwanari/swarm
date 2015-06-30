/*----------------------------------------------------------------------------
 * import
 *----------------------------------------------------------------------------*/
import swarm.objectbase.SwarmImpl;

import swarm.Globals;
import swarm.Selector;
import swarm.defobj.Zone;
import swarm.activity.ActionGroup;
import swarm.activity.ActionGroupImpl;
import swarm.activity.Activity;
import swarm.activity.Schedule;
import swarm.objectbase.Swarm;
import swarm.activity.ScheduleImpl;

import swarm.analysis.EZGraph;
import swarm.analysis.EZGraphImpl;

/*----------------------------------------------------------------------------
 * class   : SegregatedBatchSwarm
 * comment : BatchSwarm.java‚Ì“]—p
 *----------------------------------------------------------------------------*/
public class SegregatedBatchSwarm extends SwarmImpl {
  int loggingFrequency = 1;		     // Frequency of fileI/O
  
  ActionGroup displayActions;	     // schedule data structs
  Schedule displaySchedule;
  
  SegregatedModelSwarm modelSwarm;   // the Swarm we're observing
  
  EZGraph unhappinessGraph;

  public SegregatedBatchSwarm (Zone aZone) {
    super (aZone);
  }
  
  public Object buildObjects ()   {
    super.buildObjects();

    modelSwarm = (SegregatedModelSwarm)
      Globals.env.lispAppArchiver.getWithZone$key(Globals.env.globalZone,"modelSwarm"); //ƒGƒ‰[‚¾‚Á‚½ê‡‚ÌŽŸ‚Ìˆ—‚Í–¢ŽÀ‘•

    modelSwarm.buildObjects ();
    
    unhappinessGraph = new EZGraphImpl (getZone (), true);

    try {
      unhappinessGraph.createAverageSequence$withFeedFrom$andSelector
        ("unhappiness.output", modelSwarm.getAgentList(), 
         new Selector (Class.forName("SegregatedAgent"), "getUnhappiness", false));
      
    } catch (Exception e) {
      System.err.println ("Exception EZGraph: " + e.getMessage ());
    }
    
    return this;
  }  

  public Object buildActions () {
    super.buildActions ();
    
    modelSwarm.buildActions ();
    
    if (loggingFrequency > 0) {
      
      displayActions = new ActionGroupImpl (getZone ());
      
      try {
        displayActions.createActionTo$message 
          (unhappinessGraph, 
           new Selector (unhappinessGraph.getClass (), "step", true));
      } catch (Exception e) {
        System.err.println ("Exception batch EZGraph: " + e.getMessage ());
      }
      
      displaySchedule = new ScheduleImpl (getZone (), loggingFrequency);
      
      displaySchedule.at$createAction (0, displayActions);
    }
    
    return this;
  }

  public Activity activateIn (Swarm swarmContext) {
    super.activateIn (swarmContext);
    
    modelSwarm.activateIn (this);
    
    if (loggingFrequency > 0)
      displaySchedule.activateIn (this);
    
    return getActivity ();
  }
  
  public Object go () {
    System.out.println
      ("You typed `./StartSegregated --batch' " 
        + " so we're running without graphics.");
    
    System.out.println ("Segregated is running to completion.");
    
    if (loggingFrequency > 0) 
      System.out.println 
        ("It is logging data every " + loggingFrequency +
         " timesteps to: unhappiness.output");
    
    getActivity ().getSwarmActivity ().run (); 
    
    if (loggingFrequency > 0)
      unhappinessGraph.drop ();
    
    return getActivity ().getStatus ();
  }
}

/*----------------------------------------------------------------------------
 * end of file
 *----------------------------------------------------------------------------*/
