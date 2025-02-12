pos1 = [0,1.5]
pos2 = [x2,y2]
pos3 = [x3,y3]
pos4 = [x4,y4]



class main:
    def __init__(self, pos, dir, phase):
        self.pos = []
        self.dir = ()
        self.phase = 1
        #phase 1: start, move to pos1
        #phase 2: checking pos, rotating
        #phase 3: no markers seen, moving to next pos
        #phase 4: team gem seen, moving to gem, collecting gem
        #phase 5: box collected, move back to lair, deposit box, turn to face arena again
        #phase 6: sheep seen, moving to sheep, collecting sheep
        #phase 7: arena tag seen, update position
        

def start():
    move(2)
    move(2)
    move(0.5)
    analyse()
    
    
    
    

