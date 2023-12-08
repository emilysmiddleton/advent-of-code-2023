package esm.aoc.structures.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public record Range(long start, long end) {

    public Range(final long start, final long end) {
        this.start = Math.min(start, end);
        this.end = Math.max(start, end);
    }

    public boolean contains(final long value) {
        return value >= start && value <= end;
    }

    @Override
    public String toString() {
        return start + ".." + end;
    }

    public boolean overlaps(final Range other) {
        if (start > other.start) {
            return other.overlaps(this);
        }
        return end >= other.start;
    }

    public List<Long> getPointsInRange() {
        List<Long> points = new ArrayList<>();
        for (long i = start; i <= end; i++) {
            points.add(i);
        }
        return points;
    }

    public long midPoint() {
        return (end + start) / 2;
    }

    public long size() {
        return end - start + 1;
    }

    public long findFirstMatchingPoint(final Predicate<Long> matching) {
        return findFirstMatchingPoint(Long.MAX_VALUE, matching);
    }

    private long findFirstMatchingPoint(final long min, final Predicate<Long> matching) {
        final long toTest = midPoint();
        final boolean matches = matching.test(toTest);
        if (size() == 1) {
            // no further search possible
            return matches ? toTest : min;
        }
        return matches ?
                // Already matches - search lower
                new Range(start, toTest - 1).findFirstMatchingPoint(toTest, matching) :
                // Doesn't match - search higher
                new Range(toTest + 1, end).findFirstMatchingPoint(min, matching);
    }

}
