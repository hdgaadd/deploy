package org.codeman.repository;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author hdgaadd
 * Created on 2022/09/28
 */
@Data
@Accessors(chain = true)
public class Clock implements Serializable {

    private String time;

}