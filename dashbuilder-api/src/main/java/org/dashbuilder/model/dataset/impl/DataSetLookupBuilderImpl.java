/**
 * Copyright (C) 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.model.dataset.impl;

import java.util.List;

import org.dashbuilder.model.dataset.DataSetLookup;
import org.dashbuilder.model.dataset.DataSetLookupBuilder;
import org.dashbuilder.model.dataset.DataSetOp;
import org.dashbuilder.model.dataset.group.DataSetGroup;
import org.dashbuilder.model.dataset.group.DateIntervalType;
import org.dashbuilder.model.dataset.group.GroupColumn;
import org.dashbuilder.model.dataset.group.GroupFunction;
import org.dashbuilder.model.dataset.group.GroupStrategy;
import org.dashbuilder.model.dataset.group.ScalarFunctionType;
import org.dashbuilder.model.dataset.sort.DataSetSort;
import org.dashbuilder.model.date.DayOfWeek;
import org.dashbuilder.model.date.Month;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class DataSetLookupBuilderImpl implements DataSetLookupBuilder {

    private DataSetLookup dataSetLookup = new DataSetLookup();

    public DataSetLookupBuilderImpl() {
    }

    private DataSetOp getCurrentOp() {
        List<DataSetOp> dataSetOps = dataSetLookup.getOperationList();
        if (dataSetOps.isEmpty()) return null;
        return dataSetOps.get(dataSetOps.size()-1);
    }

    public DataSetLookupBuilder uuid(String uuid) {
        dataSetLookup.setDataSetUUID(uuid);
        return this;
    }

    public DataSetLookupBuilder rowOffset(int offset) {
        dataSetLookup.setRowOffset(offset);
        return this;
    }

    public DataSetLookupBuilder rowNumber(int rows) {
        dataSetLookup.setNumberOfRows(rows);
        return this;
    }

    public DataSetLookupBuilder group(String columnId) {
        return group(columnId, columnId, GroupStrategy.DYNAMIC);
    }

    public DataSetLookupBuilder group(String columnId, String newColumnId) {
        return group(columnId, newColumnId, GroupStrategy.DYNAMIC);
    }

    public DataSetLookupBuilder group(String columnId, GroupStrategy strategy) {
        return group(columnId, columnId, strategy, -1, null);
    }

    public DataSetLookupBuilder group(String columnId, DateIntervalType type) {
        return group(columnId, -1, type.toString());
    }

    public DataSetLookupBuilder group(String columnId, int maxIntervals, DateIntervalType type) {
        return group(columnId, maxIntervals, type.toString());
    }

    public DataSetLookupBuilder group(String columnId, int maxIntervals, String intervalSize) {
        return group(columnId, columnId, GroupStrategy.DYNAMIC, maxIntervals, intervalSize);
    }

    public DataSetLookupBuilder group(String columnId, String strategy, int maxIntervals, String intervalSize) {
        return group(columnId, columnId, GroupStrategy.getByName(strategy), maxIntervals, intervalSize);
    }

    public DataSetLookupBuilder group(String columnId, GroupStrategy strategy, String intervalSize) {
        return group(columnId, columnId, strategy, 0, intervalSize);
    }

    public DataSetLookupBuilder group(String columnId, GroupStrategy strategy, DateIntervalType type) {
        return group(columnId, columnId, strategy, 0, type.toString());
    }

    public DataSetLookupBuilder group(String columnId, GroupStrategy strategy, int maxIntervals, String intervalSize) {
        return group(columnId, columnId, strategy, maxIntervals, intervalSize);
    }

    public DataSetLookupBuilder group(String columnId, String newColumnId, String strategy) {
        return group(columnId, newColumnId, GroupStrategy.getByName(strategy));
    }

    public DataSetLookupBuilder group(String columnId, String newColumnId, GroupStrategy strategy) {
        return group(columnId, newColumnId, strategy, 15, null);
    }

    public DataSetLookupBuilder group(String columnId, String newColumnId, String strategy, int maxIntervals, String intervalSize) {
        return group(columnId, newColumnId, GroupStrategy.getByName(strategy), maxIntervals, intervalSize);
    }

    public DataSetLookupBuilder group(String columnId, String newColumnId, GroupStrategy strategy, int maxIntervals, String intervalSize) {
        DataSetOp op = getCurrentOp();
        if (op == null || !(op instanceof DataSetGroup)) {
            dataSetLookup.addOperation(new DataSetGroup());
        }
        DataSetGroup gOp = (DataSetGroup) getCurrentOp();
        gOp.addGroupColumn(new GroupColumn(columnId, newColumnId, strategy, maxIntervals, intervalSize));
        return this;
    }

    public DataSetLookupBuilder fixed(DateIntervalType type) {
        return fixed(type, true);
    }

    public DataSetLookupBuilder fixed(DateIntervalType type, boolean ascending) {
        DataSetGroup gOp = (DataSetGroup) getCurrentOp();
        List<GroupColumn> groupColumnList = gOp.getGroupColumns();
        if (gOp == null || groupColumnList.isEmpty()) {
            throw new RuntimeException("A domain must be configured first.");
        }

        GroupColumn groupColumn = groupColumnList.get(groupColumnList.size()-1);
        groupColumn.setStrategy(GroupStrategy.FIXED);
        groupColumn.setIntervalSize(type.toString());
        groupColumn.setAscendingOrder(ascending);
        return this;
    }

    public DataSetLookupBuilder firstDay(DayOfWeek dayOfWeek) {
        DataSetGroup gOp = (DataSetGroup) getCurrentOp();
        List<GroupColumn> groupColumnList = gOp.getGroupColumns();
        if (gOp == null || groupColumnList.isEmpty()) {
            throw new RuntimeException("A domain must is required.");
        }
        GroupColumn groupColumn = groupColumnList.get(groupColumnList.size() - 1);
        if (!GroupStrategy.FIXED.equals(groupColumn.getStrategy())) {
            throw new RuntimeException("A fixed domain is required.");
        }
        if (!DateIntervalType.DAY_OF_WEEK.equals(DateIntervalType.getByName(groupColumn.getIntervalSize()))) {
            throw new RuntimeException("A DAY_OF_WEEK fixed date domain is required.");
        }
        groupColumn.setFirstDayOfWeek(dayOfWeek);
        return this;
    }

    public DataSetLookupBuilder firstMonth(Month month) {
        DataSetGroup gOp = (DataSetGroup) getCurrentOp();
        List<GroupColumn> groupColumnList = gOp.getGroupColumns();
        if (gOp == null || groupColumnList.isEmpty()) {
            throw new RuntimeException("A domain must is required.");
        }
        GroupColumn groupColumn = groupColumnList.get(groupColumnList.size() - 1);
        if (!GroupStrategy.FIXED.equals(groupColumn.getStrategy())) {
            throw new RuntimeException("A fixed domain is required.");
        }
        if (!DateIntervalType.MONTH.equals(DateIntervalType.getByName(groupColumn.getIntervalSize()))) {
            throw new RuntimeException("A MONTH fixed date domain is required.");
        }
        groupColumn.setFirstMonthOfYear(month);
        return this;
    }

    public DataSetLookupBuilder distinct(String columnId) {
        return function(columnId, columnId, ScalarFunctionType.DISTICNT);
    }

    public DataSetLookupBuilder distinct(String columnId, String newColumnId) {
        return function(columnId, newColumnId, ScalarFunctionType.DISTICNT);
    }

    public DataSetLookupBuilder count(String newColumnId) {
        return function(null, newColumnId, ScalarFunctionType.COUNT);
    }

    public DataSetLookupBuilder min(String columnId) {
        return function(columnId, columnId, ScalarFunctionType.MIN);
    }

    public DataSetLookupBuilder min(String columnId, String newColumnId) {
        return function(columnId, newColumnId, ScalarFunctionType.MIN);
    }

    public DataSetLookupBuilder max(String columnId) {
        return function(columnId, columnId, ScalarFunctionType.MAX);
    }

    public DataSetLookupBuilder max(String columnId, String newColumnId) {
        return function(columnId, newColumnId, ScalarFunctionType.MAX);
    }

    public DataSetLookupBuilder avg(String columnId) {
        return function(columnId, columnId, ScalarFunctionType.AVERAGE);
    }

    public DataSetLookupBuilder avg(String columnId, String newColumnId) {
        return function(columnId, newColumnId, ScalarFunctionType.AVERAGE);
    }

    public DataSetLookupBuilder sum(String columnId) {
        return function(columnId, columnId, ScalarFunctionType.SUM);
    }

    public DataSetLookupBuilder sum(String columnId, String newColumnId) {
        return function(columnId, newColumnId, ScalarFunctionType.SUM);
    }

    protected DataSetLookupBuilder function(String columnId, String newColumnId, ScalarFunctionType function) {
        DataSetOp op = getCurrentOp();
        if (op == null || !(op instanceof DataSetGroup)) {
            dataSetLookup.addOperation(new DataSetGroup());
        }
        DataSetGroup gOp = (DataSetGroup) getCurrentOp();
        gOp.addGroupFunction(new GroupFunction(columnId, newColumnId, function));
        return this;
    }

    public DataSetLookupBuilder sort(String columnId, String order) {
        DataSetOp op = getCurrentOp();
        if (op == null || !(op instanceof DataSetSort)) {
            dataSetLookup.addOperation(new DataSetSort());
        }
        DataSetSort sOp = (DataSetSort) getCurrentOp();
        return this;
    }

    public DataSetLookup buildLookup() {
        return dataSetLookup;
    }
}