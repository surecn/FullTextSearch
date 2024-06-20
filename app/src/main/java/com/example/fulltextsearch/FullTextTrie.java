package com.example.fulltextsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class FullTextTrie<T> {
    private Node<T> root;
    private Map<Character, HashSet<Node<T>>> startIndexNodes;

    public FullTextTrie() {
        root = new Node<>();
        startIndexNodes = new HashMap<>();
    }

    public void insert(String word, T data) {
        Node node = root;
        for (int i = 0, len = word.length(); i < len; i++) {
            char c = word.charAt(i);
            Map<Character, Node> children = node.children;
            node = children.get(c);
            if (node == null) {
                node = new Node();
                children.put(c, node);
            }
            //处理到关键字的最后一个字符时，将对应的数据存放到这个节点
            if (i == len - 1) {
                node.data.add(data);
            }
            HashSet<Node<T>> startIndexList = startIndexNodes.get(c);
            if (startIndexList == null) {
                startIndexList = new HashSet<>();
                startIndexNodes.put(c, startIndexList);
            }
            startIndexList.add(node);
        }
    }

    public HashSet<T> search(String word) {
        if (word == null || word.length() == 0) {
            return null;
        }
        //首先查询首字符对应的节点位置
        HashSet<Node<T>> list = startIndexNodes.get(word.charAt(0));
        HashSet<T> result = new HashSet<>();
        if (list != null) {
            for (Node<T> node : list) {
                //从每个首字符所在的节点开始匹配
                Node<T> resultNode = search(word.substring(1), node);
                if (resultNode != null) {//如果有匹配到, 则收集相关结果
                    getResultInList(resultNode, result);
                }
            }
        }
        return result;
    }

    /**
     * 收集每个节点及子节点对应的结果集
     * @param node
     * @param list
     */
    private void getResultInList(Node<T> node, HashSet<T> list) {
        if (node == null) {
            return;
        }
        list.addAll(node.data);
        for (Node<T> child : node.children.values()) {
            getResultInList(child, list);
        }
    }

    private Node<T> search(String prefix, Node startNode) {
        Node<T> node = startNode;
        for (char c : prefix.toCharArray()) {
            if (!node.children.containsKey(c)) {
                return null;
            }
            node = node.children.get(c);
        }
        return node;
    }

    private static class Node<T> {
        private Map<Character, Node<T>> children;
        private List<T> data;

        public Node() {
            children = new HashMap<>();
            data = new ArrayList<>();
        }
    }

    public static void main(String[] args) {
        FullTextTrie<Integer> fullTextTrie = new FullTextTrie();
        fullTextTrie.insert("surecn", 1);
        fullTextTrie.insert("eaterm", 2);
        fullTextTrie.insert("seng", 3);
        fullTextTrie.insert("summa", 4);

        System.out.println("-==========result=============");
        HashSet<Integer> data = fullTextTrie.search("m");
        for (Integer integer : data) {
            System.out.println(String.valueOf(integer));
        }
        System.out.println("-=============================");
    }

}