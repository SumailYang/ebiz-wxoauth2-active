/*
 * Copyright (C), 2013-2015, 上海汽车集团有限公司
 * FileName: IDGenerate.java
 * Author:   zhaohuiliang
 * Date:     2015年9月22日
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.saic.framework.web.wechat.util;

import java.util.UUID;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author zhaohuiliang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 * 
 */
public class IDGenerate {

	public static String getUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}

}
