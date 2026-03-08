package com.pai.omc.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 分页结果
 *
 * @author make java
 * @since 2026-03-08
 */
@Data
@ApiModel("分页结果")
public class PageResult<T> {

    @ApiModelProperty("总记录数")
    private Long total;

    @ApiModelProperty("数据列表")
    private List<T> records;

    @ApiModelProperty("当前页码")
    private Long current;

    @ApiModelProperty("每页大小")
    private Long size;

    public static <T> PageResult<T> of(Long total, List<T> records, Long current, Long size) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(total);
        result.setRecords(records);
        result.setCurrent(current);
        result.setSize(size);
        return result;
    }
}
