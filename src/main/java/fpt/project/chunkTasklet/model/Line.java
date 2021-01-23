package fpt.project.chunkTasklet.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class Line implements Serializable {
    private String name;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate dob;
    private Long age;

    @JsonCreator
    public Line(@JsonProperty("name") String name,
                @JsonProperty("dob") @JsonFormat(pattern="yyyy-MM-dd") LocalDate dob) {
        this.name = name;
        this.dob = dob;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(this.name);
        sb.append(",");
        sb.append(this.dob.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        if (this.age != null) {
            sb.append(",");
            sb.append(this.age);
        }
        sb.append("]");
        return sb.toString();
    }
}
