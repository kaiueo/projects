__author__ = 'zhangke'

import math
import random
from tkinter import *


class UI(object):
    def __init__(self):
        self.root = Tk()
        self.root.title("RSA加密解密演示工具")
        Label(self.root, text="要加密的信息").grid(row=0, column=0)
        self.msg = StringVar()
        self.msge = Entry(self.root, textvariable=self.msg).grid(row=0, column=1)
        Label(self.root, text="要解密的信息").grid(row=1, column=0)
        self.emsg = StringVar()
        self.emsge = Entry(self.root, textvariable=self.emsg).grid(row=1, column=1)
        Label(self.root, text="结果").grid(row=2, column=0)
        self.res = StringVar()
        self.rese = Entry(self.root, textvariable=self.res).grid(row=2, column=1)
        Label(self.root, text="公钥(n, e)").grid(row=3, column=0)
        Label(self.root, text="私钥(n, d)").grid(row=4, column=0)
        self.ebtn = Button(self.root, text="加密", command=self.encryptbtn).grid(row=5, column=0)
        self.dbtn = Button(self.root, text="解密", command=self.decryptbtn).grid(row=5, column=1)
        self.rbtn = Button(self.root, text="重新生成公钥私钥", command=self.reproduce).grid(row=6)
        (self.p, self.q, self.n, self.en, self.e, self.d) = demo()
        self.public_key = StringVar()
        self.public_key.set("({0}, {1})".format(self.n, self.e))
        self.lpublic_key = Entry(self.root, textvariable=self.public_key).grid(row=3, column=1)
        self.private_key = StringVar()
        self.private_key.set("({0}, {1})".format(self.n, self.d))
        self.lprivate_key = Entry(self.root, textvariable=self.private_key).grid(row=4, column=1)
        self.root.mainloop()

    def encryptbtn(self):
        try:
            s = self.msg.get()
            pubk = eval(self.public_key.get())
            c = encrypt(pubk[0], pubk[1], s)
            self.res.set(str(c))
        except:
            self.res.set("请输入正确的公钥")

    def decryptbtn(self):
        try:
            c = self.emsg.get()
            m = eval(c)
            if type(m) is int:
                m = [m]
            prik = eval(self.private_key.get())
            s = decrypt(prik[0], prik[1], m)
            self.res.set(s)
        except ValueError:
            self.res.set("解密失败，请检测输入是否正确")
        except SyntaxError:
            self.res.set("解密失败，请检测输入是否正确")
        except:
            self.res.set("解密失败，请检测输入是否正确")

    def reproduce(self):
        (self.p, self.q, self.n, self.en, self.e, self.d) = demo()
        self.public_key.set("({0}, {1})".format(self.n, self.e))
        self.private_key.set("({0}, {1})".format(self.n, self.d))


# 计算素数表
def prime_table(n):
    result = [2, 3]
    for i in range(5, n+1, 2):
        for j in range(2, int(math.sqrt(i))+2):
            if i % j == 0:
                break
        else:
            result.append(i)

    return result


# 计算n的值
def compute_n():
    result = prime_table(10000)

    l = random.randint(700, len(result)-1)
    r = random.randint(700, len(result)-1)
    while l == r:
        l = random.randint(700, len(result)-1)
        r = random.randint(700, len(result)-1)

    return result[l], result[r], result[l] * result[r]


# 计算φ(n)的值
def euler_n(p, q):
    return (p-1)*(q-1)


# 从1到φ(n)随机选择一个与φ(n)互质的数e
def random_select_e(n):
    primes = prime_table(min(10000, n))
    index = random.randint(10, len(primes)-1)

    return primes[index]


# 扩展欧几里的算法
def ext_euclid(a, b):
    x1 = 1
    x2 = 0
    x3 = b
    y1 = 0
    y2 = 1
    y3 = a
    while y3 != 1:
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

    return y2 % b


# 加密
def encrypt(n, e, s):

    a = [ord(i) for i in s]
    c = [pow(i, e, n) for i in a]

    return c


# 解密
def decrypt(n, d, c):
    m = [pow(i, d, n) for i in c]
    a = [chr(i) for i in m]
    s = ''.join(a)

    return s


def demo():
    p, q, n = compute_n()
    en = euler_n(p, q)
    e = random_select_e(en)
    d = ext_euclid(e, en)

    return p, q, n, en, e, d


def main():
    UI()


if __name__ == "__main__":
    main()
