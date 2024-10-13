package mpoljak.dataStructures.searchTrees.KdTree;
import java.util.Comparator;
import java.lang.Integer;
/** functional interface */
interface IOperation<D extends IKdComparable<D, B>, B extends Comparable<B> > {
    void doSomething(KdNode<D, B> node);
}

public class KDTree<T extends IKdComparable<T, K>, K extends Comparable<K> > {
    private final int k;    // dimension of tree, k is from {1,2,...,n}

    private final IOperation operationPrint = (node) -> {
        System.out.println("[" + node.toString() + "], ");
    };

    private KdNode<T,K> root;


    public KDTree(int k) {
        this.k = k;
    }

    public void insert(T data) {
        /*
            * k1 <= node.k1: go left
            * if k1 > node.k1: go right
            * if level down => height++; else height--; height <0,h>
         */
        if (this.root == null) {
            this.root = new KdNode<T,K>(null, null, null, data, 1);
            return;
        }

        KdNode<T,K> currentNode = this.root;
        boolean inserted = false;
        int height = 0; // from 0, in order to start with dim = 1, which is the lowest acceptable number of dim
        int dim = Integer.MIN_VALUE;    // undefined
        int cmp = Integer.MIN_VALUE;    // undefined

        while (!inserted) {
            dim = (height % this.k) + 1;
            cmp = data.compareTo(currentNode.getData(), dim);
            if (cmp == -1 || cmp == 0) { // v------ go to the left subtree
                if (!currentNode.hasLeftSon()) {
                    KdNode<T,K> leafNode = new KdNode<T,K>(currentNode, null, null, data, dim);
                    currentNode.setLeftSon(leafNode);
                    inserted = true;
                }
                currentNode = currentNode.getLeftSon();
                height++;
            } else if (cmp == 1) {      // v------- go to the right subtree
                if (!currentNode.hasRightSon()) {
                    KdNode<T,K> leafNode = new KdNode<T,K>(currentNode, null, null, data, dim);
                    currentNode.setRightSon(leafNode);
                    inserted = true;
                }
                currentNode = currentNode.getRightSon();
                height++;
            } else {
                if (cmp == Error.INVALID_DIMENSION.getErrCode())
                    throw new java.lang.IllegalArgumentException("Node.compareTo(): Not a valid dimension");
                else if (cmp == Error.NULL_PARAMETER.getErrCode())
                    throw new NullPointerException("KdNode.compareTo(): NULL argument!");
            }
        }
        if (currentNode == null) throw new NullPointerException("CurrentNode should be inserted leaf, but is null.");

        KdNode<T, K> parent = currentNode.getParent();
        while (parent != null) {
            dim = (height % this.k) + 1;
            cmp = currentNode.getUpperBound(dim).compareTo( parent.getUpperBound(dim) );
            if (cmp == 1) { // current.upper > parent.upper
                parent.setUpperBound( currentNode.getUpperBound(dim) );
            }
            currentNode = parent;
            parent = parent.getParent();
            height--;
        }
    }

    public void find(T data) {
//        KdNode<T> currentNode = this.root;
        // k1 <= node.k1: go left
        // k1 > node.k1: go right
        // if level down => height++; else height--; height <0,h>
//        int height = 0; // from 0, in order to start with dim = 1, which is the lowest number of dim
//
//        while (currentNode != null) {
//
//            int dim = (height % this.k) + 1;
//            int cmp = data.compareTo(currentNode.getData(), dim);
//            if (cmp == -1 || cmp == 0) {
//                currentNode = currentNode.getLeftSon();
//                height++;
//            } else if (cmp == 1 ) {
//                currentNode = currentNode.getRightSon();
//                height++;
//            } else {
//                if (cmp == Error.INVALID_DIMENSION.getErrCode())
//                    throw new java.lang.IllegalArgumentException("Node.compareTo(): Not a valid dimension");
//                else if (cmp == Error.NULL_PARAMETER.getErrCode())
//                    throw new NullPointerException("Node.compareTo(): NULL argument!");
//            }
//        }
    }

    public void remove(T data) {}

    public void printTree() {
        inOrderProcessing(operationPrint);
    }

    private void inOrderProcessing(IOperation operation) {
        KdNode<T,K> current = this.root;
        boolean isLeftSonProcessed = false;
        while (current != null) {
            if (!isLeftSonProcessed) {
                if (current.hasLeftSon()) {
                    current = current.getLeftSon();
                } else { // hasRightSon
                    operation.doSomething(current);
                    if (current.hasRightSon()) {
                        current = current.getRightSon();
                    }
                    else { // hasNoneSon
                        KdNode<T,K> parent = current.getParent();
                        while (parent != null && !parent.isLeftSon(current)) {
                            current = parent;
                            parent = parent.getParent();
                        }
                        current = parent;
                        isLeftSonProcessed = true;
                    }
                }
            }
           else { // left son is processed
               operation.doSomething(current);
               if (current.hasRightSon()) {
                   current = current.getRightSon();
                   isLeftSonProcessed = false;  // continue processing left subtree of right son
               }
               else { /*  VIEM, ZE MAM LAVY STROM SPRACOVANY  A ZAROVEN NEMAM PRAVEHO SYNA, TAKZE MUSIM IST DO KORENA, RESP. ASI SOM PRESIEL VSETKY PRVKY
                          viem, ze som na uspesnom konci podstromu
                          pokial nie som v koreni && somPravySyn
                          */
                   while (current.getParent() != null && current.getParent().isRightSon(current)) {
                       current = current.getParent();
                   }
                   if (current.getParent() == null) // this could be true only for returning back to the root from
                                                    // right subtree
                       return;
                   else {
                        current = current.getParent();
                   }
               }
           }
        }
    }

    private boolean isRoot(KdNode<T,K> node) { return this.root == node; }
}
