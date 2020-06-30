import requests
import time
import re
import json
import networkx as nx
import pandas as pd
# import matplotlib.pyplot as plt


"""
REQUESTING DATA FROM INSTAGRAM
"""

tagurl_prefix = 'https://www.instagram.com/explore/tags/'

# suffix to append to tag request url to retrieve data in JSON format
tagurl_suffix = '/?__a=1'

# suffix to end cursor when requesting posts by tag
tagurl_endcursor = '&max_id='

# a generic media post preffix (concat with media shortcode to view)
posturl_prefix = 'https://www.instagram.com/p/'

def text2tags(text, striptag=True):
    pattern = '#\S+'
    text = text.lower()
    matches = re.findall(pattern, text)
    if striptag:
        matches = [match.replace('#', '') for match in matches]
    return matches

def json2posts(json_info, infilter=False):
    posts_list = json_info['graphql']['hashtag']['edge_hashtag_to_media']['edges']
    posts_dicts = []
    for post in posts_list:
        node = post['node']
        id_post = node['id']
        id_owner = node['owner']['id']
        shortcode = node['shortcode']
        edges = node['edge_media_to_caption']['edges']
        text = edges[0]['node']['text'].replace('\n', '') if len(edges) else ''
        tags = text2tags(text)
        post_url = posturl_prefix + shortcode + '/'
        post_dict = {
            'id_post': id_post,
            'id_owner': id_owner,
            'shortcode': shortcode,
            'text': text,
            'post_url': post_url,
            'tags': tags
        }
        if infilter:
            if len(tags):
                posts_dicts.append(post_dict)
        else:
            pass
    else:
        posts_dicts.append(post_dict)
    return posts_dicts

def snowball(url, deep=1, end_cursor='', count=0, showurl=False, sleep=0, forever=False, progress=False, pause=60):
    """
        :param deep: how many pages we will dive into
        :param end_cursor: controls the loading process and is handled automatically by the function
        :param showurl: to output url
        :param progress: to output the progress
        :param sleep: to slow down the function because Instagram might be blocking requests to prevent server overload
    """
    request_url = url + tagurl_endcursor + end_cursor
    if showurl:
        print(request_url)
    else:
        if progress:
            print(count, end=' ')
    while True:
        try:
            json_info = requests.get(request_url).json()
            break
        except:
            if forever:
                print('Fail, retrying in ' + str(pause) + ' seconds')
                time.sleep(pause)
            else:
                print('Fail, ' + str(count) + ' requests done')
                return []
    end_cursor = json_info['graphql']['hashtag']['edge_hashtag_to_media']['page_info']['end_cursor']
    posts = json2posts(json_info, True)
    time.sleep(sleep)
    count = count + 1
    if count < deep:
        posts += snowball(
            url=url,
            deep=deep,
            end_cursor=end_cursor,
            count=count,
            showurl=showurl,
            sleep=sleep,
            forever=forever,
            progress=progress,
            pause=pause)
    else:
        pass

    if showurl:
        pass
    else:
        if progress:
            if count == deep:
                print()
    return posts


"""
COLLECTING DATA
"""

tags = ['art', 'blackandwhite', 'artwork', 'artistsoninstagram', 'artexhibition', 'artphotography', 'artlovers']
queries = [ tagurl_prefix + tag + tagurl_suffix for tag in tags ]
data = {}

for tag, query in zip(tags, queries):
    print('Querying ' + tag + '...')
    posts = snowball(query, deep=40, forever=True, sleep=0, pause=0, progress=True)
    data[tag] = posts

# checking number of medias
for key, posts in data.items():
    print(key + ' - ' + str(len(posts)) + ' posts retrieved')

# saving data to a JSON file
f = open('data.json', 'w')
json.dump(data, f)
f.close()


# trying a limitation in the number of posts
POSTS_MAX = 100
# this list contains just edges from initial target (keys) tags to related post tags
edges_list_keys = []
# this list contains all edges between pairs of tags from the same post
edges_list_all = []

# loading previous data
file = open('data.json')
data_json = json.load(file)

# The hashtags are the nodes.
# Two nodes will be connected if they were posted together at least once.

def validate_tag(tag):
    """
    Checks if a tag is valid according to its contents and size
    """
    MAX_LEN = 25
    MIN_LEN = 1
    pattern = '^[a-zA-Z0-9]+$'
    if re.match(pattern, tag) and len(tag) < MAX_LEN and len(tag) > MIN_LEN:
        return True
    else:
        return False


"""
CREATING LISTS OF EDGES
"""

# populating the lists of edges
for person, posts in data_json.items():
    # traversing each post for each key tag
    for post in posts[:POSTS_MAX]:
        # list of tags in the post including trash tags
        post_tags = post['tags']
        # list of tags in the post after filtering
        post_tags = [tag for tag in post_tags if validate_tag(tag)]
        # list of tags without the key tag
        post_tags_drop_person = [tag for tag in post_tags if not tag == person]

        # creating edges between key tag and all others
        for tag in post_tags_drop_person:
            edge_keys = (person, tag)
            edges_list_keys.append(edge_keys)

        # creating the edges between all the tags
        for tag in post_tags:
            # index of the current tag in the list
            tag_index = post_tags.index(tag)
            # this slice is needed in order to connect all edges one and only on time
            post_tags_slice = post_tags[tag_index + 1:]
            for stag in post_tags_slice:
                edge_all_pre = (tag, stag)
                # creating the edge element in alphabetical order
                edge_all = (min(edge_all_pre), max(edge_all_pre))
                edges_list_all.append(edge_all)

print('\nNumbers of edges:')
print("Length edges_list_keys: ", len(edges_list_keys))
print("Length edges_list_all: ", len(edges_list_all))

# checking a sample of edges
# print("Sample of edges_list_all: ", edges_list_all[:10])
# print("Sample of edges_list_keys: ", edges_list_keys[:10])

"""
CREATING INITIAL GRAPH FROM LIST OF EDGES 
"""

G = nx.from_edgelist(edges_list_all)
print("Sample of G.nodes: ", list(G.nodes)[:10])
print("Sample of G.edges: ", list(G.edges())[:10])
print("Length G.nodes: ", len(G.nodes))
print("Length G.edges: ", len(G.edges))
# percentage from graph edges to list of edges
# print(100 * len(G.edges)/len(edges_list_all))

"""
GROUPING EDGES
"""

edges_df = pd.DataFrame(edges_list_all, columns=['source', 'target'])
edges_df.to_csv('edges_list_all.csv')
edges_df['tuple'] = pd.Series(zip(edges_df.source, edges_df.target))
# print(edges_df.head())
edges_grouped = edges_df.groupby('tuple').count()
# print(edges_grouped.sample(5))
edges_grouped.drop(columns='target', inplace=True, errors='ignore')
edges_grouped.columns=['weight']
edges_grouped.reset_index(inplace=True)
# print(edges_grouped.sample(5))
# print(edges_grouped.shape)
edges_grouped['source'] = edges_grouped.tuple.str[0]
edges_grouped['target'] = edges_grouped.tuple.str[1]
edges_grouped = edges_grouped.drop(columns='tuple')
print("Sample of edges_grouped: ", edges_grouped.sample(5))
edges_grouped.to_csv('edges_counted.csv')

"""
CREATING NEW GRAPH WITH THE WEIGHTS
"""

G = nx.from_pandas_edgelist(edges_grouped, edge_attr=True)
print("Sample of G.edges: ", list(G.edges(data=True))[:10])
# the same percetual as before, but now with the grouped dataframe
# print(100 * len(G.edges)/edges_grouped.shape[0])
nx.write_graphml(G, "edges_counted_" + str(POSTS_MAX) + ".graphml")
# inspecting edges
print(edges_grouped.sort_values(by='weight', ascending=False).head(10))
# inspecting weights
# print(edges_grouped.weight.sort_values(ascending=False).sample(15))
weight_counts = edges_grouped.weight.value_counts().sort_index(ascending=False)
print("Sample of weight_counts: ", weight_counts.head(10))
# print(weight_counts.tail(15))

"""
DROPPING INSIGNIFICANT EDGES
"""

TRESHOLD = 5
mask_insignificant = edges_grouped.weight.apply(lambda x : x <= TRESHOLD)
edges_grouped_dropped = edges_grouped[~mask_insignificant]
print(edges_grouped_dropped.weight.value_counts().sort_index(ascending=False).head(10))
print(edges_grouped_dropped.weight.value_counts().sort_index(ascending=False).tail(15))

# creating a new graph with dropped data
G_dropped = nx.from_pandas_edgelist(edges_grouped_dropped, edge_attr=True)
print("Sample of G_dropped: ", list(nx.selfloop_edges(G_dropped, data=True))[:10])
print("Length of dropped graph: ", len(list(nx.selfloop_edges(G_dropped, data=True))))
print("Sample of the complete graph G: ", list(nx.selfloop_edges(G, data=True))[:10])
print("Length of the complete graph: ", len(list(nx.selfloop_edges(G, data=True))))
nx.write_graphml(G_dropped, "edges_counted_" + str(POSTS_MAX) + "_dropped.graphml")

# nx.draw(G)
# plt.show()

"""
CREATING KEYS GRAPH
"""

print("Sample of edges_list_keys: ", edges_list_keys[:10])
g = nx.from_edgelist(edges_list_keys)
# len(g.nodes)
# len(g.edges)
# percentage from graph edges to list of edges
# 100 * len(g.edges)/len(edges_list_keys)

"""
GROUPING KEYS EDGES
"""

edges_df_keys = pd.DataFrame(edges_list_keys, columns=['source', 'target'])
# print(edges_df_keys.sample(5))
edges_df_keys.to_csv('edges_list_keys.csv')
edges_df_keys['tuple'] = pd.Series(zip(edges_df_keys.source, edges_df_keys.target))
# print(edges_df_keys.sample(5))
edges_grouped_keys = edges_df_keys.groupby('tuple').count()
# print(edges_grouped_keys.sample(5))
edges_grouped_keys.drop(columns='target', inplace=True, errors='ignore')
edges_grouped_keys.columns=['weight']
edges_grouped_keys.reset_index(inplace=True)
# print(edges_grouped_keys.sample(5))
edges_grouped_keys['source'] = edges_grouped_keys.tuple.str[0]
edges_grouped_keys['target'] = edges_grouped_keys.tuple.str[1]
# print(edges_grouped_keys.shape)
edges_grouped_keys = edges_grouped_keys.drop(columns='tuple')
print("Sample of edges_grouped_keys: ", edges_grouped_keys.sample(5))
edges_grouped_keys.to_csv('edges_counted_keys.csv')

"""
CREATING NEW KEYS GRAPH WITH THE WEIGHTS
"""

g = nx.from_pandas_edgelist(edges_grouped_keys, edge_attr=True)
print("Sample of key graph nodes: ", list(g.nodes)[:10])
print("Sample of key graph edges: ", list(g.edges(data=True))[:10])
# print(len(g.nodes))
# len(g.edges)
# the same percetual as before, but now with the grouped dataframe
# 100 * len(g.edges)/edges_grouped_keys.shape[0]
nx.write_graphml(g, "edges_counted_keys_" + str(POSTS_MAX) + ".graphml")
# inspecting keys edges
# print(edges_grouped_keys.sample(10))
# checking if there are any null value
# print(edges_grouped_keys.isnull().sum())
# checking different weight values
# print(edges_grouped_keys.weight.sort_values().unique())
# checking for empty tags
# print(edges_grouped_keys.source.apply( lambda x : x is '' ).sum())
# checking for empty tags
# print(edges_grouped_keys.target.apply( lambda x : x is '' ).sum())
# checking for self loop edges
# print(list(nx.selfloop_edges(g)))
# checking for swapped key tags
# key_tags = edges_grouped_keys.source.unique().tolist()
# mask_key_tags = edges_grouped_keys.target.isin(key_tags)
# print(edges_grouped_keys[mask_key_tags])

# nx.draw(g)
# plt.show()

"""
CALCULATING WEIGHTS
"""
# The node weight is the frequency in which a tag is posted in the collected data.

# creating a dictionary of weights
node_weights = {}
# populating the dictionary
for person, posts in data_json.items():
    for post in posts[:POSTS_MAX]:
        post_tags = post['tags']
        post_tags = [tag for tag in post_tags if validate_tag(tag)]
        for tag in post_tags:
            if tag in node_weights:
                node_weights[tag] = node_weights[tag] + 1
            else:
                node_weights[tag] = 1

# checking the nodes before assign weights
# print(list(G.nodes(data=True))[:10])
# checking the nodes before assign weights
# print(list(g.nodes(data=True))[:10])
# print(len(G.nodes))
# print(len(G_dropped.nodes))
# print(len(g.nodes))

"""
ASSIGNING WEIGHTS
"""

# all edges graph
nx.set_node_attributes(G, node_weights, 'weight')
print("Sample of G.nodes: ", list(G.nodes(data=True))[:10])
nx.write_graphml(G, "edges_counted_" + str(POSTS_MAX) + "_nw.graphml")
# dropped all edges graph
nx.set_node_attributes(G_dropped, node_weights, 'weight')
print("Sample of G_dropped.nodes: ", list(G_dropped.nodes(data=True))[:10])
nx.write_graphml(G_dropped, "edges_counted_" + str(POSTS_MAX) + "_dropped_nw.graphml")
# key edges graph
nx.set_node_attributes(g, node_weights, 'weight')
print("Sample of key edges graph:", list(G.nodes(data=True))[:10])
nx.write_graphml(g, "edges_counted_keys_" + str(POSTS_MAX) + "_nw.graphml")