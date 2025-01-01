#function find gem number from team value
import math
def check_camera():
    global sheeplist[] #take these out into main file later
    global gemlist[]
    
    #priority goes to gems first, then sheep. For each sheep, priority goes to the closest one it sees 

    markers = r.see()  #list of objects
    for marker in markers:

        if markers[marker].info.id (>= 100 and <= 123) or ( >= 50 and <=53) :  #if any arena tags or lair tags
            pass #call dylan's function here later

        if markers[marker].info.id == gem_value:  #priority is gems: if we see the gem then we go for that first, because points.
            gemlist.append(markers[marker])

            seen_gem = set()
            uniq_gem = []
            
            for gem in gemlist:
                if marker[gem].info.id is not in gemlist:
                    seen_gem.add(marker[gem])
                    uniq_gem.append(marker[gem])
                    marker[gem].distance = (marker[gem].distance / math.sin(90 + marker[gem].rotation)) * (math.sin(180-marker[gem].bearing - marker[gem].rotation))   #calculate new distance to centre
            
            pass #add code to move to gem here.        
            
        else:
            if markers[marker].info.id >= 0 and <= 19:
                sheeplist.append(markers[marker])
                
                seen_sheep = set()
                uniq_sheep = []
                
                for sheep in sheeplist:
                    if marker[sheep].info.id is not in sheeplist:
                        seen_sheep.add(marker[sheep])
                        uniq_sheep.append(marker[sheep])
                        marker[sheep].distance = (marker[sheep].distance / math.sin(90 + marker[sheep].rotation)) * (math.sin(180-marker[sheep].bearing - marker[sheep].rotation))   #calculate new distance to centre of the box
                        
                        
    
