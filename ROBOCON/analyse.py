#function find gem number from team value
import math
from motor import * #get rid of this once we collate all of the files 

def analyse():
    
    #priority goes to gems first, then sheep. For each sheep, priority goes to the closest one it sees 
    if len(markers) == 0:
        spin(30)
        markers = r.see()
    else:
        pass
    for marker in markers:

        if markers[marker].info.id (>= 100 and <= 123) or ( >= 50 and <=53) :  #if any arena tags or lair tags
            pass #call dylan's function here later

        if markers[marker].info.id == gem_value:  #priority is gems: if we see the gem then we go for that first, because points.
            gemlist.append(markers[marker])



            ############################################ check if gem is unique
            seen_gem = set()
            global uniq_gem
            uniq_gem = []
            
            for gem in gemlist:
                if marker[gem].info.id is not in gemlist:  #if not, calculate the distance to the centre of the gem. 
                    seen_gem.add(marker[gem])
                    uniq_gem.append(marker[gem])
                    marker[gem].distance = (marker[gem].distance / math.sin(90 + marker[gem].rotation)) * (math.sin(180-marker[gem].bearing - marker[gem].rotation))   #calculate new distance to centre
            
            pass #add code to move to gem here.        
            ############################################
        else:
            if markers[marker].info.id >= 0 and <= 19:
                sheeplist.append(markers[marker])
                
                seen_sheep = set()
                global uniq_sheep
                uniq_sheep = [] #final list of unique sheep boxes
                
                for sheep in sheeplist:
                    if marker[sheep].info.id is not in sheeplist:
                        seen_sheep.add(marker[sheep])
                        uniq_sheep.append(marker[sheep])
                        marker[sheep].distance = (marker[sheep].distance / math.sin(90 + marker[sheep].rotation)) * (math.sin(180-marker[sheep].bearing - marker[sheep].rotation))   #calculate new distance to centre of the box
                        
                        
    
