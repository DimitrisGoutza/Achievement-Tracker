package com.achievementtracker.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.metamodel.SingularAttribute;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

public class OffsetPage extends Page {
    protected int current = 1;

    public OffsetPage(int size, long totalRecords,
                      SingularAttribute defaultAttribute, SortDirection defaultDirection,
                      SingularAttribute... allowedAttributes) {
        super(size, totalRecords, defaultAttribute, defaultDirection, allowedAttributes);
    }

    public OffsetPage(int size, SingularAttribute defaultAttribute, SortDirection defaultDirection,
                      SingularAttribute... allowedAttributes) {
        super(size, defaultAttribute, defaultDirection, allowedAttributes);
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getNext() {
        return getCurrent() + 1;
    }

    public int getPrevious() {
        return getCurrent() - 1;
    }

    public int getFirst() {
        return 1;
    }

    public long getLast() {
        long lastPage = (getTotalRecords() / getSize());
        if (getTotalRecords() % getSize() == 0)
            lastPage--;

        /*
        For total = 100 size = 25, this returns 4 (the original result of the division above)
        For total = 101 size = 25, this returns 5
        */

        return lastPage + 1;
    }

    public List<Integer> getPageNumbers() {
        return IntStream.rangeClosed(
                getFirst(), new BigDecimal(getLast()).intValueExact())
                .boxed().toList();
    }

    public long getRangeStart() {
        /*
        For page = 1 size = 25, this returns 1
        For page = 2 size = 25, this returns 26
        For page = 3 size = 25, this returns 51
        For page = 4 size = 25, this returns 76
        */
        return (long) (getCurrent() - 1) * getSize() + 1;
    }

    public int getRangeStartInteger() throws ArithmeticException {
        return new BigDecimal(getRangeStart()).intValueExact();
    }

    public long getRangeEnd() {

        long firstIndex = getRangeStart();
        /*
        Subtract 1 because pages 1, 26, 51, 76 are included.
        Ranges per page [1-25]➡[26-50]➡[51-75]➡[76-100] all have a total size of 25 entries.
         */
        long pageSize = getSize() - 1;

        long lastIndex = getTotalRecords();

        /*
        In most cases we return ➡ (firstIndex + pageSize)
        Assume total = 100, size = 25, page = 3
        ➡ firstIndex = 51
        ➡ pageSize = 24
        ➡ lastIndex = 100
        ➡ min(75, 100) = 75

        Except for cases like below
        Assume total = 101, size = 25, page = 5
        ➡ firstIndex = 101
        ➡ pageSize = 24
        ➡ lastIndex = 101
        ➡ min(125, 101) = 101
        */
        return Math.min(firstIndex + pageSize, lastIndex);
    }

    public int getRangeEndInteger() throws ArithmeticException {
        return new BigDecimal(getRangeEnd()).intValueExact();
    }

    public boolean isPreviousAvailable() {
        /*
        Assume size = 25, page = 1-4
        page 1 ➡ 1 > 25 (false)
        page 2 ➡ 26 > 25 (true)
        page 3 ➡ 51 > 25 (true)
        page 4 ➡ 76 > 25 (true)
        */
        return getRangeStart() > getSize();
    }

    public boolean isNextAvailable() {
        /*
        Assume total = 101, size = 25, page = 1-5
        page 1 ➡ 101 > 25 (true)
        page 2 ➡ 101 > 50 (true)
        page 3 ➡ 101 > 75 (true)
        page 4 ➡ 101 > 100 (true)
        page 5 ➡ 101 > 101 (false)
        */
        return getTotalRecords() > getRangeEnd();
    }

    @Override
    public <T> TypedQuery<T> createQuery(EntityManager em,
                                         CriteriaQuery<T> criteriaQuery,
                                         Path attributePath) {
        /*
        Test if the sorting attribute of this page can be resolved
        against the attribute path and therefore the model used by
        the query. The method throws an exception if the sorting attribute
        of the page wasn't available on the model class referenced in
        the query.
        */

        throwIfNotApplicableFor(attributePath);

        CriteriaBuilder cb = em.getCriteriaBuilder();

        // ORDER BY clause
        Path sortPath = attributePath.get(sortAttribute);
        criteriaQuery.orderBy(
                isSortedAscending() ? cb.asc(sortPath) : cb.desc(sortPath)
        );

        TypedQuery<T> query = em.createQuery(criteriaQuery);

        // Set the offset of the query (zero based index)
        query.setFirstResult(getRangeStartInteger() - 1);

        // Set the size of the page
        if (getSize() != -1)
            query.setMaxResults(getSize());

        return query;
    }
}
