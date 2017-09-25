'''
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt, mpld3
import plotly.plotly as py
import plotly.tools as tls

#plt.style.use('ggplot')

df = pd.read_csv('/home/tenacious/SDL/PublicTransport/Dataset/Crowd.csv',index_col=0)

n = int(input())  #1571

found=False
for i in range(0,100):
    if df.iloc[i,0] == n:
        x=df.loc[i]
        found=True
        break

if found==False:
    print('NO SUCH BUS EXISTS')
    exit()


x = x.drop(x.index[0])
plt.bar(len(x),x)

mpl_fig = plt.figure()
plotly_fig = tls.mpl_to_plotly(figure)
unique_url = py.plot(plotly_fig)
print(unique_url)

#mpld3.fig_to_html(figure);
    

'''

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt, mpld3
import plotly.plotly as py
import plotly.tools as tls
import sys

df = pd.read_csv('Crowd.csv',index_col=0)
n = int(sys.argv[1])

found=False
for i in range(0,100):
    if df.iloc[i,0] == n:
        x=df.loc[i]
        found=True
        break

if found==False:
    print('NO SUCH BUS EXISTS')
    exit()
    
x = x.drop(x.index[0])
x = x[1:]
y_pos = np.arange(len(x))

fig = plt.figure(1, [7,5])
ax = fig.gca()
ax.bar(y_pos,x, align='center', alpha=0.9, width=0.6)
plt.xticks(y_pos, df.columns[2:])
plt.ylabel('Crowd')
plt.xlabel('Stops')

mpld3.save_html(fig,"graph.html")
