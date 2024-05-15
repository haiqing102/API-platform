package vin.suki.apicommon.model.vo;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderVo implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<ProductOrderVo> records;

	private long total;

	private long size;

	private long current;

	private List<OrderItem> orders;

	private boolean optimizeCountSql;

	private boolean searchCount;

	private boolean optimizeJoinOfCountSql;

	private String countId;

	private Long maxLimit;

}
