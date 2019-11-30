class Chain<E> {

    private class Node {

        private String id;

        private E value;

        private Node previous;

        Node(final String setId, 
            final E setValue, 
            final Node setPrevious) {
                id = setId;
                value = setValue;
                previous = setPrevious;
        }

        public String getId() {
            return id;
        }

        public E getValue() {
            return value;
        }

        public void setValue(final E setValue) {
            value = setValue;
        }

        public Node getPrevious() {
            return previous;
        }

        public void setPrevious(final Node setPrevious) {
            previous = setPrevious;
        }
    }

    private Node root;

    Chain() {}

    public Node add(final String id, final E value) {
        Node node = new Node(id, value, root);
        root = node;

        return node;
    }

    private Node getFromId(final Node current, final String id) {
        if (current == null || current.getId().equals(id)) {
            return current;
        }

        return getFromId(current.previous, id);
    }

    public Node getById(final String id) {
        return getFromId(root, id);
    }
}