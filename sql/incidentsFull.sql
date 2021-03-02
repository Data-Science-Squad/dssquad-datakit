SELECT incident.incident_id, incident.case_number, incident_date, incident_type.incident_type, incident_type_parent.incident_parent,
neighborhood.neighborhood, council_district.council_district, police_district.police_district, census_block_group.census_block_group, zipcode.zipcode, location.location, address.address1, address.address2
FROM incident
LEFT JOIN address ON incident.address_id = address.id
LEFT JOIN zipcode ON address.id = zipcode.zipcode
LEFT JOIN location ON address.id = location.location
LEFT JOIN neighborhood ON address.neighborhood_id=neighborhood.id 
LEFT JOIN council_district ON address.council_district_id = council_district.id
LEFT JOIN police_district ON  address.police_district_id=police_district.id
LEFT JOIN census_block_group ON address.census_block_id = census_block_group.id
LEFT JOIN incident_type ON incident.incident_type_id = incident_type.id
LEFT JOIN incident_type_parent ON incident_type.incident_parent_id = incident_type_parent.id