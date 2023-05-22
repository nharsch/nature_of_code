from math import sqrt


class PVector(object):

    def __init__(self, _x,_y):
        self.x = _x
        self.y = _y

    def __str__(self):
        return "PVector(x:{}, y:{})".format(self.x, self.y)

    def __repr__(self):
        return self.__str__()

    def add(self, v):
        x = self.x + v.x
        y = self.y + v.y
        return PVector(x, y)

    def sub(self, v):
        x = self.x - v.x
        y = self.y - v.y
        return PVector(x, y)

    def mult(self, n):
        x = self.x * n
        y = self.y * n
        return PVector(x, y)

    def div(self, n):
        x = self.x / n
        y = self.y / n
        return PVector(x, y)

    def mag(self):
        return sqrt(self.x**2 + self.y**2)

    def normalize(self):
        m = self.mag()
        if m != 0:
            return self.div(m)
        return self

    def limit(self, max):
        if self.mag()**2 > max**2:
            p = self.normalize()
            return p.mult(max)
        return self

