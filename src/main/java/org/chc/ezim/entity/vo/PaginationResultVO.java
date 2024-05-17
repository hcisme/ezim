package org.chc.ezim.entity.vo;
import java.util.ArrayList;
import java.util.List;


public class PaginationResultVO<T> {
	private Integer totalCount;
	private Integer pageSize;
	private Integer page;
	private Integer pageTotal;
	private List<T> list = new ArrayList<T>();

	public PaginationResultVO(Integer totalCount, Integer pageSize, Integer page, List<T> list) {
		this.totalCount = totalCount;
		this.pageSize = pageSize;
		this.page = page;
		this.list = list;
	}

    public PaginationResultVO(Integer totalCount, Integer pageSize, Integer page, Integer pageTotal, List<T> list) {
        if (page == 0) {
            page = 1;
        }
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.page = page;
        this.pageTotal = pageTotal;
        this.list = list;
    }

	public PaginationResultVO(List<T> list) {
		this.list = list;
	}

	public PaginationResultVO() {

	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public Integer getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(Integer pageTotal) {
        this.pageTotal = pageTotal;
    }
}
