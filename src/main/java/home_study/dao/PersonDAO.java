package home_study.dao;
import home_study.models.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
@Component
public class PersonDAO{
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public PersonDAO(JdbcTemplate jdbcTemplate){
		this.jdbcTemplate=jdbcTemplate;
	}

	public List<Person> index(){
		return jdbcTemplate.query("SELECT * FROM Person", new BeanPropertyRowMapper<>(Person.class));
	}
//       jdbcTemplate возвращает List<T>
//	     Можем не писать свой (new PersoMapper()) а использовать уже встроенный (так как Поля Класса Person и колонки в таблице совпадают)
	public Person show(int id){
		return jdbcTemplate.query("SELECT * FROM Person WHERE id=?", new Object[]{id},
				new BeanPropertyRowMapper<>(Person.class)).stream().findAny().orElse(null);
	}

	public void save(Person person){
		jdbcTemplate.update("INSERT INTO Person (name,age,email) VALUES (?,?,?)", person.getName(),person.getAge(),person.getEmail());
	}

	public void update(int id, Person updatedPerson){
		jdbcTemplate.update("UPDATE Person SET name=?, age=?, email=? where id=?", updatedPerson.getName(),
				updatedPerson.getAge(),updatedPerson.getEmail(),id);
	}

	public void delete(int id){
		jdbcTemplate.update("DELETE FROM Person where id=?",id);
	}

//	/////////////
//  Test Batch insertion
//	/////////////


	public void testBatchUpdate(){
		List<Person> people=insert1000();
		long befor=System.currentTimeMillis();

		jdbcTemplate.batchUpdate("INSERT INTO Person VALUES (?,?,?,?)",
				new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
				preparedStatement.setInt(1, people.get(i).getId());
				preparedStatement.setString(2, people.get(i).getName());
				preparedStatement.setInt(3, people.get(i).getAge());
				preparedStatement.setString(4, people.get(i).getEmail());

			}

			@Override
			public int getBatchSize() {
				return people.size();
			}
		});

		long after=System.currentTimeMillis();
		System.out.println("Time"+(after-befor));
	}

	public void testMultipleUpdate(){
		List<Person> people=insert1000();
		long befor=System.currentTimeMillis();
		for (Person p:people) {
			jdbcTemplate.update("INSERT INTO Person VALUES (?,?,?,?)",p.getId(),p.getName(),p.getAge(),p.getEmail());
		}
		long after=System.currentTimeMillis();
		System.out.println("Time"+(after-befor));
	}

	public List<Person> insert1000(){

		List<Person> people=new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			people.add(new Person(i,"Name"+i,"test"+i+"@gmail.com",30));
		}
		return people;

	}

}

