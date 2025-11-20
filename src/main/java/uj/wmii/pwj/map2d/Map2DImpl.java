package uj.wmii.pwj.map2d;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Map2DImpl<R, C, V> implements Map2D<R, C, V> {
    private final Map<R, Map<C, V>> dataRow;

    public Map2DImpl() {
        this.dataRow = new HashMap<>();
    }

    @Override
    public V put(R rowKey, C columnKey, V value) {

        if (rowKey == null || columnKey == null) {
            throw new NullPointerException("Row key and Column key can not be null");
        }

        V previousValue = get(rowKey, columnKey);

        if (!dataRow.containsKey(rowKey)) {
            dataRow.put(rowKey, new HashMap<>());
        }

        dataRow.get(rowKey).put(columnKey, value);
        return previousValue;
    }

    @Override
    public V get(R rowKey, C columnKey) {

        if (!dataRow.containsKey(rowKey)) {
            return null;
        }

        return dataRow.get(rowKey).get(columnKey);
    }

    @Override
    public V getOrDefault(R rowKey, C columnKey, V defaultValue) {

        if (containsKey(rowKey, columnKey)) {
            return get(rowKey, columnKey);
        }

        return defaultValue;
    }

    @Override
    public V remove(R rowKey, C columnKey) {

        Map<C, V> row = dataRow.get(rowKey);
        if (row == null) {
            return null;
        }

        V removedValue = row.remove(columnKey);
        if (row.isEmpty()) {
            dataRow.remove(rowKey);
        }
        return removedValue;
    }

    @Override
    public boolean isEmpty() {

        return dataRow.isEmpty();
    }

    @Override
    public boolean nonEmpty() {

        return !isEmpty();
    }

    @Override
    public int size() {

        int size = 0;
        for (Map<C, V> row : dataRow.values()) {
            size += row.size();
        }
        return size;
    }

    @Override
    public void clear() {

        dataRow.clear();
    }

    @Override
    public Map<C, V> rowView(R rowKey) {

        if (!dataRow.containsKey(rowKey)) {
            return new HashMap<>();
        }

        Map<C, V> currentRow = dataRow.get(rowKey);
        Map<C, V> rowCopy = new HashMap<>();

        for (C columnKey : currentRow.keySet()) {
            rowCopy.put(columnKey, currentRow.get(columnKey));
        }
        return rowCopy;
    }

    @Override
    public Map<R, V> columnView(C columnKey) {

        Map<R, V> columnCopy = new HashMap<>();

        for (R rowKey : dataRow.keySet()) {
            Map<C, V> rowValues = dataRow.get(rowKey);

            V value = rowValues.get(columnKey);
            if (value != null) {
                columnCopy.put(rowKey, value);
            }
        }
        return columnCopy;
    }

    @Override
    public boolean containsValue(V value) {

        for (Map<C, V> row : dataRow.values()) {
            if (row.containsValue(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsKey(R rowKey, C columnKey) {

        Map<C, V> row = dataRow.get(rowKey);
        return row != null && row.containsKey(columnKey);
    }

    @Override
    public boolean containsRow(R rowKey) {

        return dataRow.containsKey(rowKey);
    }

    @Override
    public boolean containsColumn(C columnKey) {

        for (Map<C, V> row : dataRow.values()) {
            if (row.containsKey(columnKey)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<R, Map<C, V>> rowMapView() {

        Map<R, Map<C, V>> copy = new HashMap<>();

        for (R rowKey : dataRow.keySet()) {
            Map<C, V> columnMap = new HashMap<>();

            for (C columnKey : dataRow.get(rowKey).keySet()) {
                columnMap.put(columnKey, dataRow.get(rowKey).get(columnKey));
            }
            copy.put(rowKey, columnMap);
        }
        return copy;
    }

    @Override
    public Map<C, Map<R, V>> columnMapView() {

        Map<C, Map<R, V>> copy = new HashMap<>();

        for (R rowKey : dataRow.keySet()) {
            Map<C, V> columnMap = dataRow.get(rowKey);

            for (C columnKey : columnMap.keySet()) {
                V value = columnMap.get(columnKey);

                if (!copy.containsKey(columnKey)) {
                    copy.put(columnKey, new HashMap<>());
                }
                copy.get(columnKey).put(rowKey, value);
            }
        }
        return copy;
    }

    @Override
    public Map2D<R, C, V> fillMapFromRow(Map<? super C, ? super V> target, R rowKey) {

        if (dataRow.containsKey(rowKey)) {
            Map<C, V> row = dataRow.get(rowKey);
            for (C columnKey : row.keySet()) {
                target.put(columnKey, row.get(columnKey));
            }
        }
        return this;
    }

    @Override
    public Map2D<R, C, V> fillMapFromColumn(Map<? super R, ? super V> target, C columnKey) {

        for (R rowKey : dataRow.keySet()) {
            Map<C, V> row = dataRow.get(rowKey);
            if (row.containsKey(columnKey)) {
                target.put(rowKey, row.get(columnKey));
            }
        }
        return this;
    }

    @Override
    public Map2D<R, C, V> putAll(Map2D<? extends R, ? extends C, ? extends V> source) {

        Map<? extends R, ? extends Map<? extends C, ? extends V>> sourceRows = source.rowMapView();
        for (R rowKey : sourceRows.keySet()) {
            Map<? extends C, ? extends V> columnMap = sourceRows.get(rowKey);

            if (columnMap != null) {
                this.putAllToRow(columnMap, rowKey);
            }
        }
        return this;
    }

    @Override
    public Map2D<R, C, V> putAllToRow(Map<? extends C, ? extends V> source, R rowKey) {

        if (source != null) {
            for (C columnKey : source.keySet()) {
                V value = source.get(columnKey);
                this.put(rowKey, columnKey, value);
            }
        }
        return this;
    }

    @Override
    public Map2D<R, C, V> putAllToColumn(Map<? extends R, ? extends V> source, C columnKey) {

        if (source != null) {
            for (R rowKey : source.keySet()) {
                V value = source.get(rowKey);
                this.put(rowKey, columnKey, value);
            }
        }
        return this;
    }

    @Override
    public <R2, C2, V2> Map2D<R2, C2, V2> copyWithConversion(Function<? super R, ? extends R2> rowFunction,
                                                             Function<? super C, ? extends C2> columnFunction,
                                                             Function<? super V, ? extends V2> valueFunction) {

        Map2D<R2, C2, V2> afterConversion = new Map2DImpl<>();
        for (R rowKey : dataRow.keySet()) {
            Map<C, V> columnMap = dataRow.get(rowKey);

            for (C columnKey : columnMap.keySet()) {
                V value = columnMap.get(columnKey);
                afterConversion.put(rowFunction.apply(rowKey), columnFunction.apply(columnKey), valueFunction.apply(value));
            }
        }
        return afterConversion;
    }
}
