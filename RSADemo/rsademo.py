__author__ = 'zhangke'
import math
import random



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

def computeN():
    result = primeTable(10000)

    l = random.randint(700, len(result)-1)
    r = random.randint(700, len(result)-1)
    while(l==r):
        l = random.randint(700, len(result)-1)
        r = random.randint(700, len(result)-1)

    return result[l], result[r], result[l] * result[r]

def eulerOfN(p, q):
    return (p-1)*(q-1)

def randomSelectE(n):
    primes = primeTable(min(10000, n));
    index = random.randint(10, len(primes)-1)
    return primes[index]

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

def encrypt(n, e):

    print("""所谓"加密"，就是用公钥(n, e)算出下式的c：

    　　m^e ≡ c (mod n)
""")

    s = input("请输入要加密的信息:")
    while(s==""):
        s = input("请输入要加密的信息:")

    input()
    print("计算出您所输入信息的unicode值")
    input()
    a = [ord(i) for i in s]
    print(a)
    input()
    print("计算出每个字符的加密后的值,即c")
    input()
    c = [pow(i, e, n) for i in a]
    print(c)
    return c

def decrypt(n, d, c):
    print("""解密要用私钥(n, d)算出下式的m：

    　　c^d ≡ m (mod n)
""")
    input()
    m = [pow(i, d, n) for i in c]
    print("对加密字符串的每个字符解密")
    input()
    print(m)
    a = [chr(i) for i in m]
    print("将其转换为字符串，就是我们解密后的内容")
    input()
    s = ''.join(a)
    print(s)
    return s


def demo():
    print("这是一个RSA加密解密演示程序，本程序将向您详细的演示RSA加密与解密的过程")
    input()
    print("第一步，随机选择两个不相等的质数p和q。")
    input()
    p, q, n = computeN()
    print("经过计算，我们选取了p={0}, q={1}".format(p, q))
    input()
    print("第二步，计算p和q的乘积n。")
    input()
    print("n = {0} * {1} = {2}".format(p, q, n))
    input()
    print("第三步，计算n的欧拉函数φ(n)。")
    input()
    en = eulerOfN(p, q)
    print("根据公式：φ(n) = (p-1)(q-1)，我们计算出φ({0}) = {1} * {2} = {3}".format(n, p-1, q-1, en))
    input()
    print("第四步，随机选择一个整数e，条件是1< e < φ(n)，且e与φ(n) 互质。")
    input()
    e = randomSelectE(en)
    print("我们在1到{0}之间，随即选择了{1}".format(en, e))
    input()
    print("""第五步，计算e对于φ(n)的模反元素d。\n
所谓"模反元素"就是指有一个整数d，可以使得ed被φ(n)除的余数为1。

    　　ed ≡ 1 (mod φ(n))

这个式子等价于

    　　ed - 1 = kφ(n)

于是，找到模反元素d，实质上就是对下面这个二元一次方程求解。

    　　ex + φ(n)y = 1
""")
    input()

    d = Ext_Euclid(e, en)
    y = (1-e*d)//en
    print("通过扩展欧几里得算法,我们获得一组解（{0}, {1}),即 d = {0}。至此所有计算完成.".format(d, y))
    input()
    print("第六步，将n和e封装成公钥，n和d封装成私钥。 ")
    input()
    print("在本例中， n={0}，e={1}，d={2}，所以公钥就是 ({0},{1})，私钥就是（{0}, {2}）".format(n, e, d))
    input()

    print("下面就开始加密啦")
    input()
    c = encrypt(n, e)
    input()
    print("下面开始解密")
    input()
    s = decrypt(n, d, c)
    input()
    print()
    print("演示完毕，谢谢您的观看")
    input()

def main():
    while(True):
        print("1.开始演示")
        print("2.退出")
        try:
           s = int(input("请输入相应的序号:"))
           print()
           if(s == 1):
               demo()
           elif(s==2):
                break
           else:
                print("请输入正确的序号")
                continue
        except:
            print("请输入正确的序号")


if __name__ == "__main__":
    main()






