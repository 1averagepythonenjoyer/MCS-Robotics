import math

team = r.zone()

# dictionary to store vals for each team
team_values = {
    robot.TEAM.RUBY: (24, 25, 50),    # (gem_id1, gem_id2, lairmarker_id)
    robot.TEAM.JADE: (26, 27, 51),  #not sure if "robot.TEAM.RUBY" can be stored like this. 
    robot.TEAM.TOPAZ: (28, 29, 52),
    robot.TEAM.DIAMOND: (30, 30, 53),
}

def find_team_vals():
    global team, gem_id1, gem_id2, lairmarker_id
    #gets values from dictionary
    gem_id1, gem_id2, lairmarker_id = team_values.get(team, (None, None, None))  #still sets them to none if it doesn't fit the 
    

markers = []
gemlist = []  # most valuable. List of gem objects
othergemlist = [] # second most valuable
sheeplist = [] #third most valuable 

def dist_calc(dist, rot, bear): #calculate dist. to centre of the box. Only requires one marker. 
    return (dist/math.sin(90 + rot)) * (math.sin(90 - bear - rot))

def analyse():
    markers = r.see()
    
    #priority goes to gems first. For each sheep, priority goes to the closest one it sees 

    for marker in markers:
        #arena tags
        if (marker.info.type == ARENA) or (marker.info.target_type == TARGET_TYPE.LAIR):  #if any arena tags or lair tags
            arena_update(marker.distance, marker.bearing, marker.rotation, marker.info.id)        
        
        elif marker.info.target_type == TARGET_TYPE.GEM:  #priority is gems: if we see the gem then we go for that first, because points and enemy cant get them
            #each gem will have two different codes
            if marker.info.id == gem_id1 or marker.info.id == gem_id2: #if it is our gem
                gemlist.append(marker) #record and add to list

                ############################################ check if gem is unique
                seen_ids = set()  # Track seen id values
                uniq_gem = []  #inside the subroutine because we want to clear these values each time we take a new picture etc. 
                
                for gem in gemlist:
                    gem_id = gem.info.id
                    if gem_id not in seen_ids: #if not, calculate the distance to the centre of the gem. 
                        seen_gem_ids.add(gem_id)  # Mark this gem as seen, based on id
                        uniq_gem.append(gem)
                        gem.distance = dist_calc(gem.distance, gem.rotation, gem.bearing)  # update distance
                    
                
############################################
            else:
                othergemlist.append(marker) #add to other list. 
                seen_other_gem_ids = set()
                uniq_other_gem = []

                for othergem in gemlist:
                    othergem_id = othergem.info.id
                    if othergem_id not in seen_other_gem_ids: #if not, calculate the distance to the centre of the gem. 
                        seen_other_gem_ids.add(othergem_id)  # Mark this gem as seen, based on id
                        uniq_other_gem.append(othergem)
                        othergem.distance = dist_calc(othergem.distance, othergem.rotation, othergem.bearing)  # update distance


        elif marker.info.target_type == TARGET_TYPE.SHEEP:
            sheeplist.append(marker)
                
            seen_sheep_ids = set()
            uniq_sheep = [] #final list of unique sheep boxes
                
            for sheep in sheeplist:
                sheep_id = sheep.info.id
                if sheep.info.id not in sheeplist:
                    seen_sheep_ids.add(sheep_id)
                    uniq_sheep.append(sheep)
                    sheep.distance = dist_calc(sheep.distance, sheep.rotation, sheep.bearing)   #calculate new distance to centre of the box
