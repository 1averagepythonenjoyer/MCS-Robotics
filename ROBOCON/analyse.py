import math
from operator import attrgetter  #function which will fetch the attribute from each object
import robot

r = robot.Robot()

team = r.zone

# dictionary to store vals for each team
team_values = {
    robot.TEAM.RUBY: (20, 50),    # (gem_id, lairmarker_id)
    robot.TEAM.JADE: (21, 51),  #not sure if "robot.TEAM.RUBY" can be stored like this. 
    robot.TEAM.TOPAZ: (22, 52),
    robot.TEAM.DIAMOND: (23, 53),
}

def find_team_vals():
    global team, gem_id, lairmarker_id
    #gets values from dictionary
    gem_id, lairmarker_id = team_values.get(team, (None, None))  #still sets them to none if it doesn't fit the 

find_team_vals()

markers = []
gemlist = []  # most valuable. List of gem objects
othergemlist = [] # second most valuable
sheeplist = [] #third most valuable 

#coord_memory = [] #stores coordinates of any boxes we have seen

def dist_calc(dist, rot, bear): #calculate dist. to centre of the box. Only requires one marker. 
    return (dist/math.sin(90 + rot)) * (math.sin(90 - bear - rot))

#def coord_calc(dista, beari):
    pass

def analyse():
    markers = r.see()
    
    #priority goes to gems first. For each sheep, priority goes to the closest one it sees 
    if len(markers) > 1: #only sort through the list if the list has more than 1 value. 
        print("more than 1 marker")
        for marker in markers:
            #arena tags
            if (marker.info.type == ARENA) or (marker.info.id  <= 53 and marker.info.id >= 50):  #if any arena tags or lair tags
                arena_update(marker.distance, marker.bearing, marker.rotation, marker.info.id)        
        
            elif marker.info.id <= 23 and marker.info.id >=20:  #priority is gems: if we see the gem then we go for that first, because points and enemy cant get them
                #each gem will have two different codes
                if marker.info.id == gem_id: #if it is our gem
                    gemlist.append(marker) #record and add to list
                    
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


            elif marker.info.id <= 11:  #if they are sheep
                sheeplist.append(marker)
                
                seen_sheep_ids = set()
                uniq_sheep = [] #final list of unique sheep boxes
                
                for sheep in sheeplist:
                    sheep_id = sheep.info.id
                    if sheep.info.id not in sheeplist:
                        seen_sheep_ids.add(sheep_id)
                        uniq_sheep.append(sheep)
                        sheep.distance = dist_calc(sheep.distance, sheep.rotation, sheep.bearing)   #calculate new distance to centre of the box
    else:
        print("only 1 marker")
        if markers[0].info.type == ARENA:
            arena_update(marker.distance, marker.bearing, marker.rotation, marker.info.id)
            print("Arena tag found")
        elif markers[0].info.id == gem_id:
            gemlist.append(markers[0])
            print("Our Gem found!")
        elif markers[0].info <= 23 and markers[0].info >=20:
            othergemlist.append(markers[0])
            print("Other Team's gem found!")
        elif markers[0].info.id <= 11:
            sheeplist.append(markers[0])
            print("Sheep found!")
    print("gemlist : ", gemlist)
    print("othergemlist : ", othergemlist)
    print("sheeplist : ", sheeplist)

    # if len(uniq_other_gem) > 0:
    #     uniq_other_gem.sort(key = attrgetter('dist'))  #sorts each list based on how far each marker is, from closest to furthest. 
    # if len(uniq_sheep) > 0:
    #     uniq_sheep.sort(key = attrgetter('dist'))
