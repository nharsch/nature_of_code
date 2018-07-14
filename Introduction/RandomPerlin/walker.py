class Walker(object):
    def __init__(self):
        self.tx = 0
        self.ty = 1000
        self.x = width/2
        self.y = height/2

    def step(self):
        self.x = map(noise(self.tx), 0, 1, 0, width)
        self.y = map(noise(self.ty), 0, 1, 0, height)
        self.tx += 0.01
        self.ty += 0.01
        print(self.x, self.y)

    def render(self):
        stroke(0)
        point(self.x, self.y)
