package hu.bme.mit.spaceship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.mockito.Mockito.*;

public class GT4500Test {

  private GT4500 ship;

  private TorpedoStore mockPrimaryStore;
  private TorpedoStore mockSecondaryStore;

  @BeforeEach
  public void init(){
    mockPrimaryStore = mock(TorpedoStore.class);
    mockSecondaryStore = mock(TorpedoStore.class);
    this.ship = new GT4500(mockPrimaryStore, mockSecondaryStore);
  }

  @Test
  public void fireTorpedo_Single_Success(){
    // Arrange
    when(mockPrimaryStore.fire(1)).thenReturn(true);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.SINGLE);

    // Assert
    assertEquals(true, result);
    verify(mockPrimaryStore, times(1)).fire(1);
    verify(mockSecondaryStore, times(0)).fire(1);
  }

  @Test
  public void fireTorpedo_All_Success(){
    // Arrange
    when(mockPrimaryStore.fire(1)).thenReturn(true);
    when(mockSecondaryStore.fire(1)).thenReturn(true);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.ALL);

    // Assert
    assertEquals(true, result);
    verify(mockPrimaryStore, times(1)).fire(1);
    verify(mockSecondaryStore, times(1)).fire(1);
  }

  @Test
  public void fireTorpedo_Single_First_Store_Failure() {
    // Arrange
    when(mockPrimaryStore.isEmpty()).thenReturn(false);
    when(mockPrimaryStore.fire(1)).thenReturn(false);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.SINGLE);

    // Assert
    assertEquals(false, result);
    verify(mockPrimaryStore, times(1)).fire(1);
    verify(mockSecondaryStore, times(0)).fire(1);
  }

  @Test
  public void fireTorpedo_Single_First_Store_Empty() {
    // Arrange
    when(mockPrimaryStore.isEmpty()).thenReturn(true);
    when(mockSecondaryStore.fire(1)).thenReturn(true);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.SINGLE);

    // Assert
    assertEquals(true, result);
    verify(mockPrimaryStore, times(0)).fire(1);
    verify(mockSecondaryStore, times(1)).fire(1);
  }

  @Test
  public void fireTorpedo_Single_First_Store_Empty_Multiple_Firing() {
    // Arrange
    when(mockPrimaryStore.isEmpty()).thenReturn(true);
    when(mockSecondaryStore.fire(1)).thenReturn(true);

    // Act
    ship.fireTorpedo(FiringMode.SINGLE);
    ship.fireTorpedo(FiringMode.SINGLE);
    boolean result = ship.fireTorpedo(FiringMode.SINGLE);

    // Assert
    assertEquals(true, result);
    verify(mockPrimaryStore, times(0)).fire(1);
    verify(mockSecondaryStore, times(3)).fire(1);
  }

  @Test
  public void fireTorpedo_Single_Both_Empty() {
    // Arrange
    when(mockPrimaryStore.isEmpty()).thenReturn(true);
    when(mockSecondaryStore.isEmpty()).thenReturn(true);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.SINGLE);

    // Assert
    assertEquals(false, result);
    verify(mockPrimaryStore, times(0)).fire(1);
    verify(mockSecondaryStore, times(0)).fire(1);
  }

  @Test
  public void fireTorpedo_All_Only_First_Succeeds() {
    // Arrange
    when(mockPrimaryStore.fire(1)).thenReturn(true);
    when(mockSecondaryStore.fire(1)).thenReturn(false);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.ALL);

    // Assert
    assertEquals(true, result);
    verify(mockPrimaryStore, times(1)).fire(1);
    verify(mockSecondaryStore, times(1)).fire(1);
  }

  @Test
  public void fireTorpedo_All_Failure() {
    // Arrange
    when(mockPrimaryStore.fire(1)).thenReturn(false);
    when(mockSecondaryStore.fire(1)).thenReturn(false);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.ALL);

    // Assert
    assertEquals(false, result);
    verify(mockPrimaryStore, times(1)).fire(1);
    verify(mockSecondaryStore, times(1)).fire(1);
  }

  @Test
  public void fireTorpedo_Single_Alternating_Success() {
    // Arrange
    when(mockPrimaryStore.fire(1)).thenReturn(true);
    when(mockSecondaryStore.fire(1)).thenReturn(true);

    // Act
    ship.fireTorpedo(FiringMode.SINGLE);
    boolean result = ship.fireTorpedo(FiringMode.SINGLE);

    // Assert
    assertEquals(true, result);
    verify(mockPrimaryStore, times(1)).fire(1);
    verify(mockSecondaryStore, times(1)).fire(1);
  }

  @Test
  public void fireTorpedo_Single_Secondary_Empty() {
    // Arrange
    when(mockPrimaryStore.fire(1)).thenReturn(true);
    when(mockSecondaryStore.isEmpty()).thenReturn(true);

    // Act
    ship.fireTorpedo(FiringMode.SINGLE);
    boolean result = ship.fireTorpedo(FiringMode.SINGLE);

    // Assert
    assertEquals(true, result);
    verify(mockPrimaryStore, times(2)).fire(1);
    verify(mockSecondaryStore, times(0)).fire(1);
  }

  @Test
  public void fireLaser_Failure() {
    // Act
    boolean result = ship.fireLaser(FiringMode.SINGLE);

    // Assert
    assertEquals(false, result);
    verify(mockPrimaryStore, times(0)).fire(1);
    verify(mockSecondaryStore, times(0)).fire(1);
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4 })
  public void fireTorpedo_Single_Alternating_Success(int nRounds) {
    // Arrange
    when(mockPrimaryStore.fire(1)).thenReturn(true);
    when(mockSecondaryStore.fire(1)).thenReturn(true);

    // Act
    boolean result = true;
    for (int i = 0; i < nRounds; i++) {
	result = ship.fireTorpedo(FiringMode.SINGLE);
	if (!result) break;
    }
    
    // Assert
    assertEquals(true, result);
    verify(mockPrimaryStore, times( (int)((nRounds+1)/2)) ).fire(1);
    verify(mockSecondaryStore, times( (int)(nRounds/2)) ).fire(1);
  }


}
