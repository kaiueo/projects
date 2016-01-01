__author__ = 'zhangke'

import math
import random
from tkinter import *


class UI():
    def init(self):
        self.root = Tk()
        self.root.title("RSA加密解密演示工具")
        Label(self.root, text="要加密的信息").grid(row=0,column = 0)
        self.msg = StringVar()
        self.msge = Entry(self.root,textvariable=self.msg).grid(row=0, column=1)
        Label(self.root, text="要解密的信息").grid(row=1,column = 0)
        self.emsg = StringVar()
        self.emsge = Entry(self.root,textvariable=self.emsg).grid(row=1, column=1)
        Label(self.root, text="结果").grid(row=2,column = 0)
        self.res = StringVar()
        self.rese = Entry(self.root, textvariable=self.res).grid(row=2, column=1)
        Label(self.root, text="公钥").grid(row=3,column = 0)
        Label(self.root, text="密钥").grid(row=4,column = 0)
        self.ebtn = Button(self.root,text="加密",command=self.encryptbtn).grid(row=5, column=0)
        self.dbtn = Button(self.root,text="解密", command=self.decryptbtn).grid(row=5, column=1)
        self.rbtn = Button(self.root,text="重新生成公钥密钥", command=self.reproduce).grid(row=6)
        (self.p, self.q, self.n, self.en, self.e, self.d) = demo()
        self.public_key = StringVar()
        self.public_key.set("(n, e)=({0}, {1})".format(self.n, self.e))
        self.lpublic_key = Label(self.root, textvariable = self.public_key).grid(row=3,column = 1)
        self.private_key = StringVar()
        self.private_key.set("(n, d)=({0}, {1})".format(self.n, self.d))
        self.lprivate_key = Label(self.root, textvariable=self.private_key).grid(row=4,column = 1)
        self.root.mainloop()

    def encryptbtn(self):
        s = self.msg.get()
        c = encrypt(self.n, self.e, s)
        self.res.set(c)

    def decryptbtn(self):
        c = self.emsg.get()
        m = eval(c)
        s = decrypt(self.n, self.d, m)
        self.res.set(s)

    def reproduce(self):
        (self.p, self.q, self.n, self.en, self.e, self.d) = demo()
        self.public_key.set("(n, e)=({0}, {1})".format(self.n, self.e))
        self.private_key.set("(n, d)=({0}, {1})".format(self.n, self.d))


#计算素数表
def primeTable(n):
    result = []
    result.append(2)
    result.append(3)
    for i in range(5,n+1,2):
        for j in range(2,int(math.sqrt(i))+2):
            if i%j == 0:
                break
        else:
            result.append(i)

    return result

#计算n的值
def computeN():
    result = primeTable(10000)

    l = random.randint(700, len(result)-1)
    r = random.randint(700, len(result)-1)
    while(l==r):
        l = random.randint(700, len(result)-1)
        r = random.randint(700, len(result)-1)

    return result[l], result[r], result[l] * result[r]

#计算φ(n)的值
def eulerOfN(p, q):
    return (p-1)*(q-1)

#从1到φ(n)随机选择一个与φ(n)互质的数e
def randomSelectE(n):
    primes = primeTable(min(10000, n));
    index = random.randint(10, len(primes)-1)
    return primes[index]

#扩展欧几里的算法
def Ext_Euclid (a, b):
    x1 = 1
    x2 = 0
    x3 = b
    y1 = 0
    y2 = 1
    y3 = a
    while y3!=1:
        if y3 == 0:
            return 0
        q = x3//y3
        t1 = x1-q*y1
        t2 = x2-q*y2
        t3 = x3-q*y3
        x1 = y1
        x2 = y2
        x3 = y3
        y1 = t1
        y2 = t2
        y3 = t3

    return y2%b

#加密
def encrypt(n, e, s):

    a = [ord(i) for i in s]
    c = [pow(i, e, n) for i in a]

    return c

#解密
def decrypt(n, d, c):
    m = [pow(i, d, n) for i in c]
    a = [chr(i) for i in m]
    s = ''.join(a)

    return s


def demo():
    p, q, n = computeN()
    en = eulerOfN(p, q)
    e = randomSelectE(en)
    d = Ext_Euclid(e, en)

    return (p, q, n, en, e, d)

def ebtncom():
    pass

def main():
    ui = UI()
    ui.init()


if __name__ == "__main__":
    main()