package edu.umich.library.library_identifier.normalizers;

public interface CallnumberInterface {

  public String collation_key();
  public String invalid_collation_key();
  public String any_collation_key();

  public Boolean isValid = false;
}
