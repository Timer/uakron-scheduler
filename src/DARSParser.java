import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class DARSParser {
    public static void main(final String[] params) throws Exception {
        final String s = new String(Files.readAllBytes(Paths.get("./joe.html")));
        final ArrayList<String> l = getPreBlocks(s);
        final Map<Req, List<Req>> reqs = new HashMap<>();
        for (final String pre : l) {
            if (!pre.contains("NEEDS")) continue;
            String sub = pre;
            int n;
            Req parent = null;
            while ((n = sub.indexOf("NEEDS:")) != -1) {
                sub = sub.substring(n + 6);
                String data = sub.substring(0, sub.indexOf("<")).trim().toLowerCase();
                final int nn = sub.indexOf("NEEDS:");
                final Req r = new Req(data, nn != -1 ? sub.substring(0, nn) : sub);
                if (parent != null) {
                    reqs.get(parent).add(r);
                } else reqs.put(parent = r, new LinkedList<>());
            }
        }
        final List<Need> needz = new LinkedList<>();
        for (final Map.Entry<Req, List<Req>> e : reqs.entrySet()) {
            for (final Req r : e.getValue()) {
                final String[] checks = {"course", "credit"};
                final String req = r.toString();
                final String[] cats = req.split("\\s");
                for (int cati = 0; cati < cats.length; ++cati) {
                    final String cat = cats[cati];
                    for (final String check : checks) {
                        if (cat.contains(check)) {
                            final int count = (int) Math.ceil(Double.parseDouble(cats[cati - 1]));
                            String body = "";
                            String[] bodyArr = r.body.split("\n");
                            for (final String ba : bodyArr) if (ba.contains("AcceptCourse")) body += ba;
                            needz.add(new Need(count, check, body));
                        }
                    }
                }
            }
        }

        for (final Need need : needz) {
            System.out.println(need);
            System.out.println(Arrays.toString(need.getAccepts()));
        }
    }

    private static class Accept {
        private final String c;
        private final boolean required;

        private Accept(String c) {
            c = c.trim();
            this.required = c.contains("(R)");
            if (required) c = c.replace("(R)", "").trim();
            this.c = c;
        }

        @Override
        public String toString() {
            return "{ c: " + c + ", r: " + required + " }";
        }
    }

    private static class Need {
        private final int count;
        private final String type;
        private final String data;

        public Need(final int count, final String type, final String data) {
            this.count = count;
            this.type = type;
            this.data = data;
        }

        public Accept[] getAccepts() {
            String d = data;
            int p;
            String from = "";
            while ((p = d.indexOf("<a")) != -1) {
                int oi = d.indexOf("OR");
                final boolean or = oi > -1 && oi < p;
                d = d.substring(p + 3);
                int e = d.indexOf("</a>");
                String sub = d.substring(d.indexOf(">") + 1, e);
                from += (or ? " OR " : ", ") + sub;
                d = d.substring(e);
            }
            if (from.length() > 2) from = from.substring(2);
            final String[] strs = from.split(",");
            final Accept[] accepts = new Accept[strs.length];
            int count = 0;
            for (int index = 0; index < strs.length; ++index) {
                if (strs[index].trim().length() < 1) continue;
                ++count;
                accepts[index] = new Accept(strs[index]);
            }
            return Arrays.copyOf(accepts, count);
        }

        @Override
        public String toString() {
            return "Need[count=" + count + ",type=" + type + "]";
        }
    }

    private static class Req {
        private final String s, body;

        public Req(final String s, final String body) {
            this.s = s;
            this.body = body;
        }

        @Override
        public String toString() {
            return s;
        }
    }

    private static ArrayList<String> getPreBlocks(String s) {
        final ArrayList<String> ll = new ArrayList<>();
        int index;
        final String st = "<pre>", sp = "</pre>";
        while ((index = s.indexOf(st)) != -1) {
            s = s.substring(index + st.length());
            index = s.indexOf(sp);
            if (index != -1) {
                ll.add(s.substring(0, index));
            }
        }
        return ll;
    }
}
