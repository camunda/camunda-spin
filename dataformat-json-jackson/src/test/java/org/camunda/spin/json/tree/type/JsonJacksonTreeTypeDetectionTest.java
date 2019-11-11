/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.spin.json.tree.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.camunda.spin.DataFormats.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.camunda.spin.json.mapping.Customer;
import org.camunda.spin.json.mapping.RegularCustomer;
import org.junit.Test;

public class JsonJacksonTreeTypeDetectionTest {

  @Test
  public void shouldDetectTypeFromObject() {
    RegularCustomer customer = new RegularCustomer();
    String canonicalTypeString = json().getMapper().getCanonicalTypeName(customer);
    assertThat(canonicalTypeString).isEqualTo("org.camunda.spin.json.mapping.RegularCustomer");
  }

  @Test
  public void shouldDetectListType() {
    List<Customer> customers = new ArrayList<Customer>();
    customers.add(new RegularCustomer());

    String canonicalTypeString = json().getMapper().getCanonicalTypeName(customers);
    assertThat(canonicalTypeString).isEqualTo("java.util.ArrayList<org.camunda.spin.json.mapping.RegularCustomer>");
  }

  @Test
  public void shouldDetectListTypeFromEmptyList() {
    List<RegularCustomer> customers = new ArrayList<RegularCustomer>();

    String canonicalTypeString = json().getMapper().getCanonicalTypeName(customers);
    assertThat(canonicalTypeString).isEqualTo("java.util.ArrayList<java.lang.Object>");
  }

  @Test
  public void shouldDetectSetType() {
    Set<Customer> customers = new HashSet<Customer>();
    customers.add(new RegularCustomer());

    String canonicalTypeString = json().getMapper().getCanonicalTypeName(customers);
    assertThat(canonicalTypeString).isEqualTo("java.util.HashSet<org.camunda.spin.json.mapping.RegularCustomer>");
  }

  @Test
  public void shouldDetectSetTypeFromEmptySet() {
    Set<RegularCustomer> customers = new HashSet<RegularCustomer>();

    String canonicalTypeString = json().getMapper().getCanonicalTypeName(customers);
    assertThat(canonicalTypeString).isEqualTo("java.util.HashSet<java.lang.Object>");
  }

  @Test
  public void shouldDetectMapType() {
    Map<String, Customer> customers = new HashMap<String, Customer>();
    customers.put("foo", new RegularCustomer());

    String canonicalTypeString = json().getMapper().getCanonicalTypeName(customers);
    assertThat(canonicalTypeString).isEqualTo("java.util.HashMap<java.lang.String,org.camunda.spin.json.mapping.RegularCustomer>");
  }

  @Test
  public void shouldDetectMapTypeFromEmptyMap() {
    Map<Integer, RegularCustomer> customers = new HashMap<Integer, RegularCustomer>();

    String canonicalTypeString = json().getMapper().getCanonicalTypeName(customers);
    assertThat(canonicalTypeString).isEqualTo("java.util.HashMap<java.lang.Object,java.lang.Object>");
  }

  @Test
  public void shouldHandleNullParameter() {
    try {
      json().getMapper().getCanonicalTypeName(null);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // happy path
    }
  }

  @Test
  public void shouldHandleListOfLists() {
    List<List<RegularCustomer>> nestedCustomers = new ArrayList<List<RegularCustomer>>();
    List<RegularCustomer> customers = new ArrayList<RegularCustomer>();
    customers.add(new RegularCustomer());
    nestedCustomers.add(customers);

    String canonicalTypeString = json().getMapper().getCanonicalTypeName(nestedCustomers);
    assertThat(canonicalTypeString).isEqualTo("java.util.ArrayList<java.util.ArrayList<org.camunda.spin.json.mapping.RegularCustomer>>");
  }

  @Test
  public void shouldHandleListOfSets() {
    List<Set<RegularCustomer>> nestedCustomers = new ArrayList<Set<RegularCustomer>>();
    Set<RegularCustomer> customers = new HashSet<RegularCustomer>();
    customers.add(new RegularCustomer());
    nestedCustomers.add(customers);

    String canonicalTypeString = json().getMapper().getCanonicalTypeName(nestedCustomers);
    assertThat(canonicalTypeString).isEqualTo("java.util.ArrayList<java.util.HashSet<org.camunda.spin.json.mapping.RegularCustomer>>");
  }

  @Test
  public void shouldHandleSetOfSets() {
    Set<Set<RegularCustomer>> nestedCustomers = new HashSet<Set<RegularCustomer>>();
    Set<RegularCustomer> customers = new HashSet<RegularCustomer>();
    customers.add(new RegularCustomer());
    nestedCustomers.add(customers);

    String canonicalTypeString = json().getMapper().getCanonicalTypeName(nestedCustomers);
    assertThat(canonicalTypeString).isEqualTo("java.util.HashSet<java.util.HashSet<org.camunda.spin.json.mapping.RegularCustomer>>");
  }

  @Test
  public void shouldHandleSetOfLists() {
    Set<List<RegularCustomer>> nestedCustomers = new HashSet<List<RegularCustomer>>();
    List<RegularCustomer> customers = new ArrayList<RegularCustomer>();
    customers.add(new RegularCustomer());
    nestedCustomers.add(customers);

    String canonicalTypeString = json().getMapper().getCanonicalTypeName(nestedCustomers);
    assertThat(canonicalTypeString).isEqualTo("java.util.HashSet<java.util.ArrayList<org.camunda.spin.json.mapping.RegularCustomer>>");
  }

  @Test
  public void shouldHandleMapOfMaps() {
    Map<String, Map<Integer, RegularCustomer>> nestedCustomers = new HashMap<String, Map<Integer, RegularCustomer>>();
    Map<Integer, RegularCustomer> customers = new HashMap<Integer, RegularCustomer>();
    customers.put(42, new RegularCustomer());
    nestedCustomers.put("foo", customers);

    String canonicalTypeString = json().getMapper().getCanonicalTypeName(nestedCustomers);
    assertThat(canonicalTypeString).isEqualTo("java.util.HashMap<java.lang.String,java.util.HashMap<java.lang.Integer,org.camunda.spin.json.mapping.RegularCustomer>>");
  }

}
