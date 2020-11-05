package org.jack.common.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.springframework.util.CollectionUtils;

public class Logic {
    public static class LogicNode {
        public static final int NODE_TYPE_DATA = 0;
        public static final int NODE_TYPE_AND = 1;
        public static final int NODE_TYPE_OR = 2;
        public static final int NODE_TYPE_NOT = 3;
        private final int nodeType;
        private Object data;

        private LogicNode(int nodeType, Object data) {
            this.nodeType = nodeType;
            this.data = data;
        }

        public static <D> LogicNode of(D... data) {
            List<D> dataList = new ArrayList<>();
            for (D item : data) {
                dataList.add(item);
            }
            return new LogicNode(NODE_TYPE_DATA, dataList);
        }

        public static <D> LogicNode of(List<D> data) {
            return new LogicNode(NODE_TYPE_DATA, data);
        }

        public static LogicNode and(List<LogicNode> nodes) {
            return new LogicNode(NODE_TYPE_AND, nodes);
        }

        public static LogicNode or(List<LogicNode> nodes) {
            return new LogicNode(NODE_TYPE_OR, nodes);
        }

        public static LogicNode not(LogicNode node) {
            return new LogicNode(NODE_TYPE_NOT, node);
        }

        public int getNodeType() {
            return nodeType;
        }

        public Object getData() {
            return data;
        }

        public static boolean isData(LogicNode node) {
            final int nodeType = node.nodeType;
            final Object data = node.data;
            if (nodeType == NODE_TYPE_NOT) {
                return data != null;
            } else if (nodeType == NODE_TYPE_OR || nodeType == NODE_TYPE_AND) {
                List<LogicNode> list = (List<LogicNode>) data;
                return list != null && !list.isEmpty();
            }
            return true;
        }

        public static String toText(LogicNode node) {
            final int nodeType = node.nodeType;
            final Object data = node.data;
            StringBuilder sb = new StringBuilder();
            if (nodeType == NODE_TYPE_DATA) {
                List<?> list = (List<?>) data;
                if (list.size() == 1) {
                    sb.append(list.get(0));
                } else {
                    sb.append("(");
                    int i = 0;
                    for (Object item : list) {
                        if (i++ == 0) {
                            sb.append(item);
                        } else {
                            sb.append(",").append(item);
                        }
                    }
                    sb.append(")");
                }
                return sb.toString();
            } else if (nodeType == NODE_TYPE_NOT) {
                LogicNode noted = (LogicNode) data;
                return "!" +toText(noted);
            } else {
                List<LogicNode> list = (List<LogicNode>) data;
                sb.append("(");
                int i = 0;
                for (LogicNode item : list) {
                    if (i++ == 0) {
                        sb.append(toText(item));
                    } else {
                        if (nodeType == NODE_TYPE_AND) {
                            sb.append(" and ");
                        } else {
                            sb.append(" or ");
                        }
                        sb.append(toText(item));
                    }
                }
                sb.append(")");
                return sb.toString();
            }
        }

        @Override
        public String toString() {
            String text=toText(this);
            if(text.startsWith("(")&&text.endsWith(")")){
                text.substring(1, text.length()-1);
            }
            return text;
        }
    }

    private static class SimpleHolder {
        private List<LogicNode> nodeList = new ArrayList<>();
        private LogicNode last = null;

        private boolean validate() {
            if (last == null || last.nodeType == LogicNode.NODE_TYPE_NOT && last.data == null) {
                return false;
            }
            return true;
        }

        private <T> void addData(T data) {
            if (last != null) {
                if (last.nodeType == LogicNode.NODE_TYPE_DATA) {
                    ((List<T>) last.data).add(data);
                    return;
                } else if (last.nodeType == LogicNode.NODE_TYPE_NOT && last.data == null) {
                    last.data = LogicNode.of(data);
                    return;
                }
            }
            addNode(LogicNode.of(data));
        }

        private boolean addNode(LogicNode node) {
            if (last != null && last.nodeType == LogicNode.NODE_TYPE_NOT && last.data == null) {
                if (!(node.nodeType == LogicNode.NODE_TYPE_NOT || LogicNode.isData(node))) {
                    return false;
                }
                last.data = node;
            } else {
                nodeList.add(node);
            }
            last = node;
            return true;
        }
    }

    public static <T> Result<LogicNode> parseLogicNode(List<T> dataList, T not, T or, T and) {
        SimpleHolder simpleHolder = new SimpleHolder();
        Stack<SimpleHolder> stack = new Stack<>();
        stack.add(simpleHolder);
        for (T data : dataList) {
            LogicNode node = null;
            if ("(".equals(data)) {
                simpleHolder = new SimpleHolder();
                stack.add(simpleHolder);
                continue;
            } else if (")".equals(data)) {
                if (stack.size() < 2 || !simpleHolder.validate()) {
                    return Result.fail("语法错误！");
                }
                Result<LogicNode> result = parseSimple(simpleHolder.nodeList);
                if (!result.isSuccess()) {
                    return result;
                } else {
                    node = (LogicNode) result.getData();
                }
                stack.pop();
                simpleHolder = stack.peek();
            } else if (not.equals(data)) {
                node = LogicNode.not(null);
            } else if (or.equals(data)) {
                node = LogicNode.or(new ArrayList<>());
            } else if (and.equals(data)) {
                node = LogicNode.and(new ArrayList<>());
            } else {
                simpleHolder.addData(data);
                continue;
            }
            if (!simpleHolder.addNode(node)) {
                return Result.fail("语法错误！");
            }
        }
        if (stack.size() != 1 || !simpleHolder.validate()) {
            return Result.fail("语法错误！");
        }
        Result<LogicNode> result = parseSimple(simpleHolder.nodeList);
        if (!result.isSuccess()) {
            return result;
        }
        marge((LogicNode) result.getData());
        return result;
    }

    private static void marge(LogicNode node) {
        final int nodeType = node.nodeType;
        if (nodeType == LogicNode.NODE_TYPE_AND || nodeType == LogicNode.NODE_TYPE_OR) {
            List<LogicNode> useList = new ArrayList<LogicNode>();
            List<LogicNode> nodeList = (List<LogicNode>) node.data;
            for (LogicNode itemNode : nodeList) {
                marge(itemNode);
                if (itemNode.nodeType == nodeType) {
                    useList.addAll((List<LogicNode>) itemNode.data);
                } else {
                    useList.add(itemNode);
                }
            }
            node.data = useList;
        }
    }

    private static <T> Result<LogicNode> parseSimple(List<LogicNode> nodeList) {
        if (CollectionUtils.isEmpty(nodeList)) {
            return Result.fail("语法错误！");
        }
        List<LogicNode> orList = new ArrayList<LogicNode>();
        LogicNode root = null;
        Boolean lastIsData = null;
        for (LogicNode node : nodeList) {
            boolean currIsData = LogicNode.isData(node);
            if (lastIsData != null) {
                if (lastIsData == currIsData) {
                    return Result.fail("语法错误！");
                }
            }
            if (root == null) {
                if (!currIsData) {
                    return Result.fail("语法错误！");
                }
                root = node;
                orList.add(root);
            } else {
                if (node.nodeType == LogicNode.NODE_TYPE_OR) {
                    root = null;
                    if (!lastIsData) {
                        return Result.fail("语法错误！");
                    }
                } else {
                    if (node.nodeType == LogicNode.NODE_TYPE_AND) {
                        ((List<LogicNode>) node.data).add(root);
                        root = node;
                    } else {
                        ((List<LogicNode>) root.data).add(node);
                    }
                }
            }
            lastIsData = currIsData;
        }
        if (!lastIsData) {
            return Result.fail("语法错误！");
        }
        if (orList.size() == 1) {
            return Result.success(root);
        } else {
            return Result.success(LogicNode.or(orList));
        }
    }
}