def analyse():
    markers_raw = r.see()
    for i in range(len(markers_raw)):
        markers.append(markers_raw[i-1])
        print(markers[i-1])

    
    #priority goes to gems first. For each sheep, priority goes to the closest one it sees 
    if len(markers) > 1: #only sort through the list if the list has more than 1 value. 
        print("more than 1 marker")
        for j in range(len(markers)):
            #arena tags
            if (markers[i-1].info.type == "MARKER_TYPE.ARENA") or (markers[i-1].info.id  <= 53 and markers[i-1].info.id >= 50):  #if any arena tags or lair tags
                arena_update(markers[i-1].dist, markers[i-1].bearing.y, markers[i-1].rotation.z, markers[i-1].info.id)        
        
            elif markers[i-1].info.id <= 23 and markers[i-1].info.id >=20:  #priority is gems: if we see the gem then we go for that first, because points and enemy cant get them
                #each gem will have two different codes
                if markers[i-1].info.id == gem_id: #if it is our gem
                    gemlist.append(markers[i-1]) #record and add to list
                    
############################################
                else:
                    othergemlist.append(markers[i-1]) #add to other list. 
                    seen_other_gem_ids = set()
                    uniq_other_gem = []

                    for othergem in gemlist:
                        othergem_id = othergem.info.id
                        if othergem_id not in seen_other_gem_ids: #if not, calculate the distance to the centre of the gem. 
                            seen_other_gem_ids.add(othergem_id)  # Mark this gem as seen, based on id
                            uniq_other_gem.append(othergem.info)
                            othergem.distance = dist_calc(othergem.dist, othergem.rotation.z, othergem.bearing.y)  # update distance


            elif marker.info.id <= 11:  #if they are sheep
                sheeplist.append(markers[i-1])
                
                seen_sheep_ids = set()
                uniq_sheep = [] #final list of unique sheep boxes
                
                for sheep in sheeplist:
                    sheep_id = sheep.info.id
                    if sheep.info.id not in sheeplist:
                        seen_sheep_ids.add(sheep_id)
                        uniq_sheep.append(sheep.info)
                        sheep.distance = dist_calc(sheep.dist, sheep.rotation.z, sheep.bearing.y)   #calculate new distance to centre of the box
    else:
        print("only 1 marker")
        if markers[0].info.type == "MARKER_TYPE.ARENA":
            arena_update(markers[0].dist, markers[0].bearing.y, marker.rotation.z, markers[0].info.id)
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
