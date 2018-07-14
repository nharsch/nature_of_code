class Walker(object):
    def __init__(self):
        self.x = width/2
        self.y = height/2

    def step(self):
        stepx = random(-1,1)
        stepy = random(-1,1)
        self.x += stepx
        self.y += stepy
        print(self.x, self.y)

    def render(self):
        stroke(0)
        point(self.x, self.y)



