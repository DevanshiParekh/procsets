package com.semaifour.facesix.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Graph extends JSONMap {

	private static final long serialVersionUID = 7507096655029088599L;

}
