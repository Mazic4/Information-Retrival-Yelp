import pandas as pd
import subprocess


path = '/home/huayi/Desktop/Spark/dataset/'
file = 'review.json'

chunksize = 50000

data = pd.read_json(path+file,lines= True,chunksize=chunksize)

chunk = 0
n_reviews = 0

try:
    while next(data) is not None:
        df = pd.DataFrame(data.next())

        reviews = df[['review_id','text']]

        i = 0
        for review in reviews['text']:
            with open('/home/huayi/Desktop/Spark/hlta/hlta-master/Source/'+str(i)+'.txt','w') as text_file:
                text_file.write(review.encode('utf-8'))
            i += 1

        subprocess.call(['/home/huayi/Desktop/Spark/hlta/hlta-master/shellscript.sh',str(chunk)])

        chunk += 1
        n_reviews += df.shape[0]
except:
    print 'Total ', n_reviews, 'files are processed, by ', chunk, 'chunks'