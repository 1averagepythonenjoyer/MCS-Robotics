def search_labels():
    global sheep_dist_compare

    markers = r.see()  #list of objects
    for marker in markers:
        



    
    Gems = r.see(look_for = Gem.Team)  #looks for our gem. Placeholder var. rn because docs don't say how to reference it properly
    Sheeps = r.see(look_for = Sheep)


    #Sheeps = r.see(look_for=Sheep)  #ROBOCON docs is outdated doesn't dictate id of the sheep. Might actually be a number, in which case we'll need an inequality. 
    #for Sheep in Sheeps:
        #print(Sheep.info.type)
        #print(Sheep.info.id)

    #dist_compare = sorted(Sheeps, key=lambda Sheep: Sheep.dist)  #sorts the sheeps by distance. lowest to highest
    
    Gem = r.see(look_for=Gem)  #again, not sure about the item ID here
